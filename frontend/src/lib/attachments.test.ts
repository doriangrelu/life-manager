import { describe, expect, it } from 'vitest'
import { attachmentCountLabel, formatSize, rejectionOf, remainingSlots } from './attachments'

describe('pièces jointes', () => {
  it('affiche une taille lisible', () => {
    expect(formatSize(850)).toBe('850 o')
    expect(formatSize(12 * 1024)).toBe('12 Ko')
    expect(formatSize(2.4 * 1024 * 1024)).toBe('2,4 Mo')
  })

  it('refuse avant l\'envoi un fichier vide ou de plus de 10 Mo', () => {
    expect(rejectionOf({ size: 0 })).toBe('Fichier vide')
    expect(rejectionOf({ size: 10 * 1024 * 1024 + 1 })).toBe('Fichier trop lourd (10 Mo au plus)')
    expect(rejectionOf({ size: 10 * 1024 * 1024 })).toBeNull()
  })

  it('compte les places restantes sur 10', () => {
    expect(remainingSlots(3)).toBe(7)
    expect(remainingSlots(12)).toBe(0)
  })

  it('accorde le libellé au nombre', () => {
    expect(attachmentCountLabel(1)).toBe('1 pièce jointe')
    expect(attachmentCountLabel(3)).toBe('3 pièces jointes')
  })
})
