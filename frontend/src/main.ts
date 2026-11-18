import { mount } from 'svelte'
// Police servie par l'application elle-même : disponible hors-ligne, aucun appel à un service externe.
import '@fontsource-variable/plus-jakarta-sans'
import './app.css'
import App from './App.svelte'

const app = mount(App, {
  target: document.getElementById('app')!,
})

export default app
