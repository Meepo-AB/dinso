---
name: dinso-design
description: Defines the visual language and UX rules for Dinso, a simplified pension and insurance demo. Use when creating or changing Dinso pages, portals, demo-profile login, seeded-data views, components, styles, or interaction states.
---

# Dinso design

## Product intent

Dinso is a credible, usable demo for complex pension and insurance work—not a miniature marketing site. It communicates those workflows through a small, legible product surface with realistic seeded data and no real integrations.

The visual stance is **calm, structured, reassuring, and practical**. Use generous whitespace, clear task hierarchy, data-first views, readable forms, cards for bounded actions, and step-based flows. Give Dinso its own neutral demo identity; do not reuse customer names, logos, branded fonts, or customer-specific colour palettes.

The primary mode is **Operate**. Every screen should help a demo visitor understand their role, recognize the current context, inspect data, and complete a small action.

## Design principles

1. **Make the demo self-explanatory.** Label mock outcomes, demo-only actions, and simulated external steps honestly. Never imply a transaction, signature, BankID authentication, payment, or integration occurred.
2. **Show breadth through meaningful variation.** Seeded data must contain examples with different statuses, values, and next actions—not repeated placeholder rows.
3. **Preserve focus.** Each portal overview has one primary next action. Secondary actions belong in row menus, a side panel, or a detail view.
4. **Data earns its space.** Use tables for comparison and administration; use KPI blocks for a small set of at-a-glance numbers; use cards for choices, summaries, and contained tasks. Do not turn every datum into a card.
5. **Progressive disclosure over dense screens.** Start with a useful overview, then open details in an in-page panel, drawer, or focused route. Avoid nested modals.
6. **Make state visible.** Status, deadlines, errors, empty states, and completed actions must be understandable without relying on colour alone.
7. **Keep demo interaction reversible.** Confirm simulated changes locally, show feedback, and offer a reset-demo path. Do not mimic irreversible production flows.

## Information architecture

### Entry and identity

The entry screen is **Välj demo-profil**, not a password form.

- Present profiles as clearly labelled selectable cards or a compact list.
- Each profile shows: name, role, portal, short purpose, and a compact data-preview line such as company or member count.
- Selecting a profile enters the portal immediately or through one explicit button: `Öppna demo`.
- Keep a visible profile switcher in the authenticated header. It must state that this is a demo and make switching roles easy.
- Do not use BankID, passwords, personal identity numbers, MFA, consent flows, or real-looking authentication claims.

### Portal model

Design portal navigation around role and task. Use a shared shell with a portal-specific accent and navigation set.

| Portal | Purpose | Core navigation |
| --- | --- | --- |
| Privat | Understand personal pension and insurance, see documents and take simple service actions. | Översikt, Försäkringar, Händelser, Dokument |
| Företag | Administer a company's insured people, agreements, tasks and documents. | Översikt, Medarbetare, Ärenden, Avtal, Dokument |
| Rådgivare | Get a cross-company work queue and inspect client cases. Include only if seeded data supports it. | Översikt, Kunder, Ärenden |
| Administration | Demonstrate operational overview and controlled content/data states. Keep it intentionally narrow. | Översikt, Demo-data |

Only expose a portal when at least one demo profile is allowed to enter it. Hide unavailable capabilities instead of showing disabled integration-based menu items.

### Screen anatomy

Use this consistent order:

1. **App header:** Dinso wordmark, portal name, profile switcher, optional compact demo badge.
2. **Primary navigation:** persistent on desktop; accessible compact menu on small screens.
3. **Context header:** eyebrow/context, page title, one-sentence explanation when needed, then the primary action.
4. **Overview content:** summary first, then work queue or data table, followed by supporting detail.
5. **Feedback region:** inline success/error state and toast notifications for completed local demo actions.

Keep the main content column between `72rem` and `120rem`. Use full-width data tables within that constraint; do not create an application that is visually constrained to a centred stack of narrow cards.

## Visual language

### Brand and colour

Dinso has one shared interaction language, but every customer build has its own theme. Customer themes may change colour, surface treatment, corner radius and visual tone; they must not change information hierarchy, semantic status meanings, component behaviour or accessibility.

The three customer directions are:

- SvenskeBanken - MörkBlått, cremevitt, hårda kanter
- PensionsBolaget - Grönt, beige, mjuka former
- FinBanken- Ljusare blått, vitt, neutrala kanter

#### SvenskeBanken

SvenskeBanken should feel established, precise and bank-like.

- **Ink / header:** deep navy `#102A43`; **primary:** `#174E7A`; **hover:** `#123B5C`.
- **Canvas / surface:** warm cream `#F7F1E5` and white `#FFFFFF`; **border:** muted sand `#D9CFBF`.
- Use strong horizontal header bands, clear table rules and firm button shapes.
- Corner radius is `0–4px`. Avoid pills, bubbly cards and large soft shadows.
- Use restrained gold/sand only as a supporting detail, never as a competing action colour.

#### PensionsBolaget

PensionsBolaget should feel warm, long-term and people-centred without becoming informal.

- **Ink:** forest `#173F35`; **primary:** `#28755D`; **hover:** `#1E5B47`.
- **Canvas:** soft beige `#F3EEE4`; **surface:** `#FFFCF7`; **border:** `#D8D1C5`.
- Use gentle tonal panels, more breathing room around summaries, and restrained organic visual details such as a soft accent shape in an otherwise quiet header.
- Corner radius is `12px` for inputs and compact cards, `16px` for large panels. Shadows remain subtle and diffuse.
- Use green for actions and navigation; semantic success remains separately labelled and must not be inferred from the theme colour.

#### FinBanken

FinBanken should feel clear, contemporary and investment-oriented.

- **Ink:** deep slate-blue `#1E334A`; **primary:** lighter blue `#3B82B6`; **hover:** `#286A9A`.
- **Canvas / surface:** cool white `#F7FAFC` and white `#FFFFFF`; **border:** neutral blue-grey `#D7E0E8`.
- Use clean white data surfaces, calm contrast and compact financial comparison patterns. Charts, fund allocation and transaction data can use the blue scale with accessible non-blue series colours.
- Corner radius is `6–8px`: neither sharp like SvenskeBanken nor soft like PensionsBolaget.
- Keep decoration to a minimum; clarity and financial data scanability are the distinguishing cues.

Use colour semantically and sparingly. Maintain WCAG AA contrast. Never represent a state with colour alone: pair status colours with a text label and, where appropriate, an icon.

Shared semantic tokens remain customer-independent: **warning** `#A85F00`, **danger** `#B42318`, and **success** `#18794E`, each with a pale semantic background. Do not recolour danger or warning states to match a customer's brand.

Define a shared token contract for `--color-ink`, `--color-canvas`, `--color-surface`, `--color-border`, `--color-primary`, `--color-primary-strong`, `--radius-control`, `--radius-panel` and semantic status tokens. Each customer overlay supplies values for that contract; do not scatter literal colour values across Vue components.

### Typography and spacing

Use the system UI stack: `Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif`. Do not import a brand font merely to imitate a customer brand.

- Base size: `16px`; compact table metadata may use `14px`.
- Page title: `32–40px` desktop, `28–32px` mobile; weight 700.
- Section title: `22–26px`; weight 700.
- Body: `16px`, line-height at least `1.5`.
- Use tabular numerals for financial and date-heavy columns.
- Use an 8px spacing scale: `4, 8, 16, 24, 32, 40, 48, 64`.

Prefer short Swedish labels and plain language: `Pågående ärenden`, `Nästa steg`, `Senast uppdaterad`, `Ändring sparad i demon`.

### Surfaces and controls

- Use the active customer's `--radius-control` and `--radius-panel` tokens. Do not mix hard, soft and neutral corner treatments within one customer build.
- Borders should carry most separation. Shadows are subtle and reserved for raised menus, drawers and dialogs; SvenskeBanken uses the least shadow and PensionsBolaget may use the softest.
- Primary buttons are filled ink/blue with white text. Secondary actions are outlined or text buttons. Destructive actions are never styled as the primary action unless explicitly confirmed.
- Inputs have persistent labels above the field; placeholders are examples, not labels.
- Use chips for short stable metadata or filters, not for every field value.
- Use clear empty-state panels with a specific explanation and one helpful next action.

## Components and interaction

Build reusable Vue components before page-specific markup where the pattern occurs twice or more:

- `AppShell`, `PortalNav`, `DemoProfileSwitcher`, `PageHeader`
- `MetricGrid`, `StatusBadge`, `DataTable`, `FilterBar`, `EmptyState`
- `ActionCard`, `TaskList`, `DetailDrawer`, `ConfirmationDialog`, `Toast`
- `DemoNotice` for locally simulated behaviour and `ResetDemoButton` where changing state is possible.

For a create/edit flow, use a short stepper only when the user genuinely needs sequential information. Use one focused question or form per step; show a plain-language review before confirmation. A simulated integration should resolve into an explicit local state such as `Markerad som skickad i demon`—not a fake external response.

Use motion only to explain hierarchy: 150–200ms opacity/transform transitions for drawers, menus and feedback. Respect `prefers-reduced-motion`.

## Seeded-data presentation

Seed data is product content, not filler. Each principal list should include enough examples to demonstrate:

- active, pending, completed and attention-needed states;
- different dates, monetary amounts, names and organisations;
- both populated and empty/zero-result states where relevant;
- at least one row that leads to a useful details view or local action.

Use clearly fictional Swedish people, companies, document names and identifiers. Do not use production personal data, real policy numbers, or logos. Keep currency in SEK and dates in Swedish locale (`10 sep. 2026`) when displayed to the user.

Show demo provenance once per relevant screen or action—never on every table row. Example: `Det här är demo-data. Ändringar sparas bara i den här sessionen.`

## Responsive and accessible behaviour

- Design desktop first for administration tables, then make the task—not the desktop layout—responsive.
- On narrow screens, preserve the most important row fields, allow horizontal table scrolling with a visible affordance, or turn a table row into an accessible summary/detail pattern. Do not silently drop essential information.
- Provide keyboard-visible focus states, semantic headings, labelled controls, table headers/scope, and accessible status announcements.
- Use native buttons and links for their respective actions. All dialogs and drawers must trap focus, restore focus on close, and support Escape.
- Avoid icon-only actions unless the accessible name is unambiguous; tooltips supplement rather than replace labels.

## Implementation guardrails

- Vue 3, TypeScript, Vite, Pinia, Vue Router, SCSS, Vitest and Vue Test Utils.
- Replace integrations with local services or seeded stores. UI code must consume interfaces, not hard-code fake integration responses in components.
- Keep portal permissions enforced by the selected demo profile. The UI must not expose an unauthorised route just because a user changes a URL.
- Use Swedish UI copy by default. Add translation structure only when it is needed by the demo scope.
- Validate changed views with the project's targeted unit/type checks. For UI work, follow the local `impeccable` skill as well.

## Do not

- Add dashboards made only of generic KPI cards when a work queue or table is the real task.
- Hide important data behind hover-only controls or rely on tooltips for core instructions.
- Use lorem ipsum, vague mock data, or fake production confirmations.
