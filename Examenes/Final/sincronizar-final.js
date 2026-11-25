// Copia los bancos de preguntas de los dos parciales dentro de Cuestionario-Final.html,
// para que el final siga siendo un solo archivo que se abre con doble clic.
//
// Uso (desde esta carpeta):   node sincronizar-final.js
//
// Corrélo cada vez que agregues o corrijas preguntas en Cuestionario-Parcial-1.html o
// Cuestionario-Parcial-2.html. Las preguntas integradoras del final se editan directamente
// en Cuestionario-Final.html (bloque PF) y este script no las toca.
const fs = require('fs');
const path = require('path');

const aqui = __dirname;
const FINAL = path.join(aqui, 'Cuestionario-Final.html');
const FUENTES = [
  { marca: '1ER PARCIAL', archivo: path.join(aqui, '..', 'Primer Parcial', 'Cuestionario-Parcial-1.html') },
  { marca: '2DO PARCIAL', archivo: path.join(aqui, '..', 'Segundo parcial', 'Cuestionario-Parcial-2.html') }
];

function extraerBanco(html, archivo) {
  const ini = html.indexOf('const T = {');
  const inicioBanco = html.indexOf('const BANCO = [', ini);
  const fin = html.indexOf('\n];', inicioBanco);
  if (ini < 0 || inicioBanco < 0 || fin < 0) throw new Error(`No encontré "const T" / "const BANCO" en ${archivo}`);
  return html.slice(ini, fin + 3);
}

let final = fs.readFileSync(FINAL, 'utf8');
for (const { marca, archivo } of FUENTES) {
  const banco = extraerBanco(fs.readFileSync(archivo, 'utf8'), archivo);
  const abre = `/* <<< ${marca} */`;
  const cierra = `/* ${marca} >>> */`;
  const a = final.indexOf(abre), c = final.indexOf(cierra);
  if (a < 0 || c < 0 || c < a) throw new Error(`No encontré los marcadores ${abre} ... ${cierra} en el final`);
  final = final.slice(0, a + abre.length) + '\n' + banco + '\n' + final.slice(c);
  const n = (banco.match(/^\{id:'/gm) || []).length;
  console.log(`${marca}: ${n} preguntas copiadas desde ${path.relative(aqui, archivo)}`);
}

// Verifica que el bloque de datos resultante se pueda ejecutar antes de escribir.
const script = final.match(/<script>([\s\S]*?)<\/script>/)[1];
const datos = script.slice(0, script.indexOf('/* ---------- Utilidades'));
const { BANCO } = new Function(datos + '\nreturn { BANCO };')();
fs.writeFileSync(FINAL, final);
console.log(`Listo: el final tiene ${BANCO.length} preguntas en total.`);
