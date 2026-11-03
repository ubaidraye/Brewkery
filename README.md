# Brewkery

A native Android coffee and bakery ordering app built in Kotlin with Jetpack Compose. It was built as the Clickretina Android take-home assignment.

Flow: Menu, then Item Detail, then Cart, then Order Status.

## Features

- **Menu:** store banner (delivery time and fee from the API), category filter chips, and an item list with thumbnail, badge, rating and base price.
- **Item detail:** hero image, description, ingredients, and selectable size, milk option and sugar level. The price recalculates live and is multiplied by quantity.
- **Cart:** quantity controls, Clear Cart, and a summary with subtotal, delivery fee and tax.
- **Order status:** placing an order clears the cart, generates a ticket ID (for example `BK-74921`) and shows the `PREPARING` status with the estimated wait.
- **Loading and error states** on the network call, with a Retry button.
## Tech stack

- Kotlin, Jetpack Compose, Material 3
- MVVM with a repository layer
- Retrofit with the Gson converter, and Kotlin Coroutines
- StateFlow for UI state (`Loading`, `Success`, `Error`)
- Coil for image loading
- Navigation Compose
- Hilt for dependency injection
- JUnit for unit tests
## Architecture

- **Data layer:** data classes matching the API JSON, a Retrofit service, and a repository that returns a `Result`.
- **ViewModels:** expose a `UiState` sealed class through StateFlow, and the Compose screens collect it.
- **Cart:** a single `CartRepository` shared by all screens holds the cart as a StateFlow, so it survives navigation.
- **Dependency injection:** Hilt modules in the `di` package provide the app's dependencies, and `BrewkeryApplication` is the Hilt entry point.
- **Pricing logic:** kept in a separate pure Kotlin class so it can be unit tested without Android.
- **Totals:** subtotal is the sum of item prices (base price plus selected options, times quantity). Delivery fee comes from the API. Tax is 8% of the subtotal, rounded to 2 decimals. Example: subtotal 9.40 + delivery 2.50 + tax 0.75 = 12.65.
## API

- `GET https://raw.githubusercontent.com/VivekShah138/Brewkery/main/data.json` for store config and the full menu.
- `GET https://raw.githubusercontent.com/VivekShah138/Brewkery/main/api/items/{id}.json` for a single item.
## How to run

1. Clone the repo and open it in Android Studio.
2. Let Gradle sync, then run the app on an emulator or a device.
3. Run the unit tests with `./gradlew test`.
   A debug APK was also sent with the submission.

## Tests

Unit tests cover the pricing and cart total logic: option adjustments, quantity, subtotal, delivery fee, tax rounding, and an empty cart.

## How I used AI

**Tools:** Gemini in Android Studio for generating code, and Claude for planning the build, drafting my prompts and reviewing the approach. I read and tested the generated code myself.

**Prompts I sent to Gemini (abridged):**

1. "I'm building a native Android app called Brewkery in Kotlin with Jetpack Compose, MVVM, Retrofit, Coroutines, StateFlow, Coil and Navigation Compose. Here is the JSON from the API: [data.json]. Create the data classes, the Retrofit service, a repository that returns a Result, and a ViewModel exposing a UiState sealed class via StateFlow. Do not build any UI yet."
2. "Build the Menu screen in Jetpack Compose: a store banner, a horizontal category chips row that filters the list, and a list of items showing thumbnail, badge, title, rating and base price. Show a loading indicator and an error message with a Retry button."
3. "Build the Cart screen with item quantity controls, Clear Cart, and a summary with subtotal, delivery fee and tax (8% of the subtotal only, rounded to 2 decimals). Place Order clears the cart, generates a ticket ID and navigates to the Order Status screen."
   **What AI got right:** Gemini built the Menu and Item Detail screens correctly: the category filtering, the item list with images loaded through Coil, and the live price recalculation for size, milk and sugar options.

**What AI got wrong, and how I fixed it:**

1. **Summary card placement.** On the Cart screen, Gemini placed the summary card right after the item cards, so it appeared near the top instead of at the bottom. I fixed it by giving the items column `Modifier.weight(1f)`, so the list takes the remaining space and the summary card stays at the bottom.
2. **Status bar spacing.** The generated screens had wrong spacing under the status bar. which I fixed by applying the status bar padding to the .
3. **Reusable Components** Gemini didn't extract composable components into separate reusable file it just created a single file containing all the composable which took about 800 lines, after extracting the composables, the lines dropped to about 90 lines! which increases readability and promotes separation of concerns
## Notes

- The cart is held in memory, so it resets when the app closes. No local database is used, as the brief allows.
