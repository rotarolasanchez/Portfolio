import SwiftUI
import shared

/// Vista raíz de la app iOS.
/// Monta el UIViewController de Compose Multiplatform como vista SwiftUI.
struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.all) // Compose maneja sus propios insets
    }
}

/// Wrapper UIViewControllerRepresentable que integra Compose en SwiftUI.
struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        // Llama a la función Kotlin definida en MainiOS.kt
        // Inicializa Koin y retorna el ComposeUIViewController con App()
        MainiOSKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // No se requiere actualización manual; Compose maneja su propio estado
    }
}

#Preview {
    ContentView()
}

