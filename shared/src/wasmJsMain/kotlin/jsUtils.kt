/**
 * Declaraciones externas de utilidades JS para Kotlin/WasmJs.
 * WasmJs requiere @JsFun en vez de external fun/external object.
 */

@JsFun("(message) => alert(message)")
external fun alert(message: String)

@JsFun("(message) => console.log(message)")
external fun consoleLog(message: String)

@JsFun("(message) => console.error(message)")
external fun consoleError(message: String)
