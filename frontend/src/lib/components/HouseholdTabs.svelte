<script lang="ts">
  import type { UserProfile } from '../api/types'
  import { rise } from '../motion'
  import { spaceOf } from '../navigation'
  import { rememberSpaceSection } from '../preferences'
  import { navigate, route, type Section } from '../route'
  import { scrollToTop } from '../scroll'
  import SpaceTabs from '../ui/molecules/SpaceTabs.svelte'
  import MainNav from '../ui/organisms/MainNav.svelte'
  import BudgetView from './budget/BudgetView.svelte'
  import ChargesView from './charges/ChargesView.svelte'
  import HouseholdView from './HouseholdView.svelte'
  import RemindersBanner from './RemindersBanner.svelte'
  import ShoppingView from './shopping/ShoppingView.svelte'
  import VehiclesView from './vehicle/VehiclesView.svelte'

  // Le foyer sélectionné : ses écrans, regroupés par espaces (Argent, Courses, Maison, Foyer).
  let {
    householdId,
    profile,
    onaddhousehold,
    onhouseholdgone,
    onhouseholdchanged,
  }: {
    householdId: string
    profile: UserProfile
    onaddhousehold: () => void
    /** Foyer quitté ou supprimé depuis cet appareil : l'application bascule, avec ce message. */
    onhouseholdgone: (message: string) => void
    /** Foyer renommé : la liste des foyers (en-tête) est rechargée. */
    onhouseholdchanged: () => void
  } = $props()

  // L'écran vient de l'adresse.
  const section = $derived($route.section)

  // Chaque écran s'ouvre en haut, et devient le dernier consulté de son espace.
  $effect(() => {
    rememberSpaceSection(spaceOf(section).id, section)
    scrollToTop()
  })

  const open = (next: Section) => navigate({ section: next })
</script>

<RemindersBanner {householdId} refreshKey={section} />

<SpaceTabs active={section} onselect={open} />

<!-- Changement d'écran : le nouveau apparaît en fondu (l'ancien disparaît aussitôt). -->
{#key section}
  <div class="stack" in:rise>
    {#if section === 'budget'}
      <BudgetView {householdId} {profile} />
    {:else if section === 'charges'}
      <ChargesView {householdId} {profile} />
    {:else if section === 'shopping'}
      <ShoppingView {householdId} />
    {:else if section === 'vehicles'}
      <VehiclesView {householdId} />
    {:else}
      <HouseholdView
        {householdId}
        currentUserId={profile.userId}
        {onaddhousehold}
        ongone={onhouseholdgone}
        onrenamed={onhouseholdchanged}
      />
    {/if}
  </div>
{/key}

<MainNav active={section} onselect={open} />
