<script lang="ts">
  import { shortMonth, type BarSeries } from '../../evolution'

  // Graphique mois par mois en SVG : des barres (une ou plusieurs par mois), une courbe facultative
  // et des repères facultatifs (un trait pointillé par mois, pour le prévu). Supporte les valeurs négatives.
  // Toucher un mois le sélectionne : le parent affiche alors ses chiffres exacts (`selected`).

  let {
    months,
    bars,
    line = null,
    marks = null,
    selected = $bindable(null),
    label,
  }: {
    months: string[]
    bars: BarSeries[]
    line?: { values: (number | null)[]; color: string } | null
    marks?: (number | null)[] | null
    selected?: number | null
    /** Description du graphique pour les lecteurs d'écran. */
    label: string
  } = $props()

  const COLUMN = 30
  const TOP = 8
  const BASE = 120 // bas de la zone de dessin (les mois s'écrivent dessous)
  const width = $derived(months.length * COLUMN)

  const all = $derived([
    ...bars.flatMap((b) => b.values),
    ...(line?.values ?? []).filter((v): v is number => v !== null),
    ...(marks ?? []).filter((v): v is number => v !== null),
  ])
  const max = $derived(Math.max(1, ...all))
  const min = $derived(Math.min(0, ...all))
  /** Position verticale d'un montant (le zéro n'est en bas que sans valeur négative). */
  const y = (cents: number) => TOP + ((max - cents) / (max - min)) * (BASE - TOP)
  const zero = $derived(y(0))

  const barWidth = $derived(Math.min(14, 22 / Math.max(1, bars.length)))
  const barX = (month: number, serie: number) => month * COLUMN + (COLUMN - barWidth * bars.length) / 2 + serie * barWidth

  // Courbe : segments entre mois consécutifs qui ont une valeur.
  const points = $derived(
    (line?.values ?? [])
      .map((v, i) => (v === null ? null : `${i * COLUMN + COLUMN / 2},${y(v)}`))
      .filter((p): p is string => p !== null)
      .join(' '),
  )
  // Avec beaucoup de mois, un libellé sur deux.
  const step = $derived(months.length > 12 ? 2 : 1)
</script>

<svg viewBox="0 0 {width} 140" class="chart" role="img" aria-label={label}>
  {#each months as month, i (month)}
    {#if selected === i}
      <rect class="highlight" x={i * COLUMN + 1} y="0" width={COLUMN - 2} height={BASE + 4} rx="6" />
    {/if}
  {/each}
  <line class="axis" x1="0" x2={width} y1={zero} y2={zero} />
  {#each bars as serie, s (serie.name)}
    {#each serie.values as value, i (i)}
      {#if value !== 0}
        <rect
          x={barX(i, s)}
          y={Math.min(y(value), zero)}
          width={barWidth - 1.5}
          height={Math.max(1, Math.abs(y(value) - zero))}
          rx="2"
          fill={serie.colorAt(i)}
        />
      {/if}
    {/each}
  {/each}
  {#if marks}
    {#each marks as mark, i (i)}
      {#if mark !== null}
        <line class="mark" x1={i * COLUMN + 3} x2={(i + 1) * COLUMN - 3} y1={y(mark)} y2={y(mark)} />
      {/if}
    {/each}
  {/if}
  {#if line && points}
    <polyline class="line" points={points} stroke={line.color} />
    {#each line.values as value, i (i)}
      {#if value !== null}
        <circle cx={i * COLUMN + COLUMN / 2} cy={y(value)} r="2.5" fill={line.color} />
      {/if}
    {/each}
  {/if}
  {#each months as month, i (month)}
    {#if i % step === 0}
      <text class="month" x={i * COLUMN + COLUMN / 2} y="134">{shortMonth(month)}</text>
    {/if}
    <!-- Zone qu'on touche pour lire les chiffres du mois. -->
    <rect
      class="hit"
      x={i * COLUMN}
      y="0"
      width={COLUMN}
      height="140"
      role="button"
      tabindex="0"
      aria-label="Détail de {shortMonth(month)}"
      aria-pressed={selected === i}
      onclick={() => (selected = selected === i ? null : i)}
      onkeydown={(e) => (e.key === 'Enter' || e.key === ' ') && (e.preventDefault(), (selected = selected === i ? null : i))}
    />
  {/each}
</svg>

<style>
  .chart {
    display: block;
    width: 100%;
    /* Sur grand écran, le graphique ne grossit pas au-delà d'une taille lisible. */
    max-width: 560px;
    margin: 0 auto;
    height: auto;
    overflow: visible;
  }
  .highlight {
    fill: var(--color-surface-muted);
  }
  .axis {
    stroke: var(--color-border);
    stroke-width: 1;
  }
  .mark {
    stroke: var(--color-text-muted);
    stroke-width: 1.5;
    stroke-dasharray: 3 2;
  }
  .line {
    fill: none;
    stroke-width: 2.5;
    stroke-linejoin: round;
    stroke-linecap: round;
  }
  .month {
    font-size: 9px;
    font-weight: 600;
    text-anchor: middle;
    fill: var(--color-text-muted);
  }
  .hit {
    fill: transparent;
    cursor: pointer;
    outline: none;
  }
  .hit:focus-visible {
    fill: var(--color-primary-soft);
    fill-opacity: 0.5;
  }
</style>
