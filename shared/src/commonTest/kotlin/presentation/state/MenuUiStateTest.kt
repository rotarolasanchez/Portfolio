package presentation.state

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MenuUiStateTest {

    @Test
    fun `default state has default menu items`() {
        val state = MenuUiState()

        assertEquals(4, state.menuItems.size)
        assertEquals("Home", state.menuItems[0])
        assertEquals("Profile", state.menuItems[1])
        assertEquals("Settings", state.menuItems[2])
        assertEquals("Help", state.menuItems[3])
    }

    @Test
    fun `default state has Home selected`() {
        val state = MenuUiState()
        assertEquals("Home", state.selectedItem)
    }

    @Test
    fun `default icons list is empty`() {
        val state = MenuUiState()
        assertTrue(state.menuIcons.isEmpty())
    }

    @Test
    fun `update selected item`() {
        val state = MenuUiState()
        val newState = state.copy(selectedItem = "Profile")

        assertEquals("Profile", newState.selectedItem)
        // Los demás campos no cambian
        assertEquals(4, newState.menuItems.size)
    }

    @Test
    fun `custom menu items`() {
        val state = MenuUiState(
            menuItems = listOf("ChatBot", "Perfil", "Configuración"),
            selectedItem = "ChatBot"
        )

        assertEquals(3, state.menuItems.size)
        assertEquals("ChatBot", state.selectedItem)
    }

    @Test
    fun `selecting different items`() {
        var state = MenuUiState(
            menuItems = listOf("ChatBot", "Perfil", "Config")
        )

        // Simular selección de items
        state = state.copy(selectedItem = "ChatBot")
        assertEquals("ChatBot", state.selectedItem)

        state = state.copy(selectedItem = "Perfil")
        assertEquals("Perfil", state.selectedItem)

        state = state.copy(selectedItem = "Config")
        assertEquals("Config", state.selectedItem)
    }
}

