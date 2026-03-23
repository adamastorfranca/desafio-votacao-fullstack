import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

function removeComments(str) {
  return str
    // Remove block comments /* ... */
    .replace(/\/\*[\s\S]*?\*\//g, '')
    // Remove line comments // ... but avoid https:// etc. by checking no colon precedes //
    .replace(/(?<!:)\/\/.*$/gm, '')
    // Remove empty spaces left on lines
    .replace(/^[ \t]+$/gm, '')
    // Collapse multiple blank lines into max 2
    .replace(/\n{3,}/g, '\n\n')
    // Remove newlines at the start of file
    .replace(/^\n+/, '');
}

function processDir(dir) {
  const files = fs.readdirSync(dir);
  for (const file of files) {
    const full = path.join(dir, file);
    if (fs.statSync(full).isDirectory()) {
      processDir(full);
    } else if (full.endsWith('.ts') || full.endsWith('.tsx')) {
      const code = fs.readFileSync(full, 'utf8');
      const newCode = removeComments(code);
      if (newCode !== code) {
        fs.writeFileSync(full, newCode, 'utf8');
        console.log('Cleaned', full);
      }
    }
  }
}

processDir(path.join(__dirname, 'src'));
