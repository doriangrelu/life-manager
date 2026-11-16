import { accessToken, login } from '../auth/auth'

/** Erreur renvoyée par l'API, avec un message lisible par l'utilisateur. */
export class ApiError extends Error {
  readonly status: number

  constructor(status: number, message: string) {
    super(message)
    this.status = status
  }
}

/** Appelle l'API en ajoutant le jeton d'accès ; renvoie vers Keycloak si la session a expiré. */
export async function apiRequest<T>(
  method: 'GET' | 'POST' | 'PUT' | 'DELETE',
  path: string,
  body?: unknown,
): Promise<T> {
  const token = await accessToken()
  if (!token) {
    await login()
    throw new ApiError(401, 'Connexion nécessaire')
  }
  const response = await fetch(`/api${path}`, {
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
    throw new ApiError(response.status, await errorMessage(response))
  }
  // 204 : succès sans contenu (ex. suppression).
  return (response.status === 204 ? undefined : await response.json()) as T
}

// Format d'erreur Micronaut : { message, _embedded: { errors: [{ message }] } }.
async function errorMessage(response: Response): Promise<string> {
  try {
    const json = await response.json()
    return json?._embedded?.errors?.[0]?.message ?? json?.message ?? 'Erreur inattendue'
  } catch {
    return 'Erreur inattendue'
  }
}
