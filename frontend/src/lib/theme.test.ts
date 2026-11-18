import { readFileSync } from 'node:fs'
import { describe, expect, it } from 'vitest'
import { contrast } from './color'

// Vérifie le contraste (WCAG AA : 4,5:1) des couples texte / fond du thème, en clair et en sombre.
// Les couleurs sont lues directement dans app.css : le test suit le thème réellement livré.

const css = readFileSync(new URL('../app.css', import.meta.url), 'utf8')

function variables(block: string): Record<string, string> {
  const pairs = [...block.matchAll(/--(color-[\w-]+):\s*(#[0-9a-f]{6})/gi)]
  return Object.fromEntries(pairs.map(([, name, value]) => [name, value]))
}

const light = variables(css.split('@media (prefers-color-scheme: dark)')[0])
const dark = { ...light, ...variables(css.split('@media (prefers-color-scheme: dark)')[1]) }

// [texte, fond] : chaque texte de l'interface sur chaque fond où il apparaît.
const PAIRS: [string, string][] = [
  ['color-text', 'color-bg'],
  ['color-text', 'color-surface'],
  ['color-text', 'color-surface-muted'],
  ['color-text-muted', 'color-bg'],
  ['color-text-muted', 'color-surface'],
  ['color-text-muted', 'color-surface-muted'],
  ['color-on-primary', 'color-primary'],
  ['color-primary-text', 'color-surface'],
  ['color-primary-text', 'color-bg'],
  ['color-on-primary-soft', 'color-primary-soft'],
  ['color-on-success-soft', 'color-success-soft'],
  ['color-on-warning-soft', 'color-warning-soft'],
  ['color-danger', 'color-danger-soft'],
  ['color-danger', 'color-surface'],
]

describe.each([
  ['clair', light],
  ['sombre', dark],
])('thème %s', (_, theme) => {
  it.each(PAIRS)('%s sur %s atteint 4,5:1', (text, background) => {
    expect(contrast(theme[text], theme[background])).toBeGreaterThanOrEqual(4.5)
  })
})
