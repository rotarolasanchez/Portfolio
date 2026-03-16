// Fix: 'Cannot use import.meta outside a module'
//
// CAUSA RAÍZ: webpack usa devtool='eval-source-map' que envuelve cada módulo
// en eval("...código..."). El código dentro de eval() NO hereda el contexto
// de módulo ES, por lo que import.meta lanza SyntaxError aunque el script
// se cargue con <script type="module">.
//
// FIX: Cambiar a 'source-map' que genera archivos .map externos sin eval().
// Con <script type="module"> en index.html + source-map, import.meta.url
// funciona correctamente para localizar el .wasm de Kotlin/Wasm.
config.devtool = 'source-map';
