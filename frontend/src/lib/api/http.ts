import { accessToken, login } from '../auth/auth'
import { reportNotFound } from '../householdAccess'

/** Erreur renvoyée par l'API, avec un message lisible par l'utilisateur. */
export class ApiError extends Error {
  readonly status: number

  constructor(status: number, message: string) {
    super(message)
    this.status = status
  }
}

const UNREACHABLE = 'Serveur injoignable : vérifiez votre connexion, puis réessayez.'
const SERVER_ERROR = 'Le serveur a rencontré un problème. Réessayez dans un instant.'

/** Appelle l'API en ajoutant le jeton d'accès ; renvoie vers Keycloak si la session a expiré. */
export async function apiRequest<T>(
  method: 'GET' | 'POST' | 'PUT' | 'DELETE',
  path: string,
  body?: unknown,
): Promise<T> {
  const token = await requireToken()
  const response = await send(`/api${path}`, {
    method,
    headers: {
      Authorization: `Bearer ${token}`,
      ...(body === undefined ? {} : { 'Content-Type': 'application/json' }),
    },
    body: body === undefined ? undefined : JSON.stringify(body),
  })
  if (response.status === 401) {
    await login()
  }
  if (!response.ok) {
    if (response.status === 404) {
      reportNotFound(path)
    }
    throw new ApiError(response.status, await errorMessage(response))
  }
  // 204 : succès sans contenu (ex. suppression).
  return (response.status === 204 ? undefined : await response.json()) as T
}

/** Jeton d'accès, ou renvoi vers Keycloak si la session a expiré. */
async function requireToken(): Promise<string> {
  const token = await accessToken()
  if (!token) {
    await login()
    throw new ApiError(401, 'Connexion nécessaire')
  }
  return token
}

// fetch échoue sans réponse (réseau coupé, serveur arrêté) : message clair au lieu de « Failed to fetch ».
async function send(url: string, init: RequestInit): Promise<Response> {
  try {
    return await fetch(url, init)
  } catch {
    throw new ApiError(0, UNREACHABLE)
  }
}

/**
 * Envoie un fichier (multipart, champ « file ») en suivant la progression : fetch ne la donne pas,
 * d'où XMLHttpRequest. {@code onprogress} reçoit un ratio entre 0 et 1.
 */
export async function apiUpload<T>(path: string, file: File, onprogress: (ratio: number) => void): Promise<T> {
  const token = await requireToken()
  const form = new FormData()
  form.append('file', file, file.name)
  return new Promise<T>((resolve, reject) => {
    const request = new XMLHttpRequest()
    request.open('POST', `/api${path}`)
    request.setRequestHeader('Authorization', `Bearer ${token}`)
    request.upload.onprogress = (event) => event.lengthComputable && onprogress(event.loaded / event.total)
    request.onerror = () => reject(new ApiError(0, 'Envoi impossible : vérifiez votre connexion, puis réessayez.'))
    request.onload = () => {
      if (request.status >= 200 && request.status < 300) {
        resolve(JSON.parse(request.responseText) as T)
      } else {
        reject(new ApiError(request.status, uploadErrorMessage(request)))
      }
    }
    request.send(form)
  })
}

function uploadErrorMessage(request: XMLHttpRequest): string {
  if (request.status === 413) {
    return 'Fichier trop lourd (10 Mo au plus)'
  }
  try {
    return readableMessage(request.status, JSON.parse(request.responseText))
  } catch {
    return readableMessage(request.status, null)
  }
}

/** Télécharge un contenu binaire avec le jeton (jamais dans l'URL). */
export async function apiBlob(path: string): Promise<Blob> {
  const token = await requireToken()
  const response = await send(`/api${path}`, { headers: { Authorization: `Bearer ${token}` } })
  if (!response.ok) {
    throw new ApiError(response.status, await errorMessage(response))
  }
  return response.blob()
}

// Format d'erreur Micronaut : { message, _embedded: { errors: [{ message }] } }.
async function errorMessage(response: Response): Promise<string> {
  try {
    return readableMessage(response.status, await response.json())
  } catch {
    return readableMessage(response.status, null)
  }
}

/**
 * Message à montrer : celui de l'API pour une demande refusée (400, 404, 409…), toujours en français ;
 * un message générique pour une panne du serveur, dont le texte technique n'aide pas l'utilisateur.
 */
export function readableMessage(status: number, json: unknown): string {
  if (status >= 500) {
    return status === 503 ? (apiMessage(json) ?? SERVER_ERROR) : SERVER_ERROR
  }
  return apiMessage(json) ?? 'Demande refusée. Réessayez.'
}

function apiMessage(json: unknown): string | null {
  const body = json as { message?: string; _embedded?: { errors?: { message?: string }[] } } | null
  return body?._embedded?.errors?.[0]?.message ?? body?.message ?? null
}
