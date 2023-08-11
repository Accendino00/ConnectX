const { exec } = require('child_process');
const fs = require('fs');
const path = require('path');

const SRC_DIR = 'src';
const BIN_DIR = 'bin';

async function compileJavaFiles(dir) {
  try {
    const entries = await fs.promises.readdir(dir);

    for (const entry of entries) {
      const entryPath = path.join(dir, entry);
      const stats = await fs.promises.lstat(entryPath);

      if (stats.isDirectory()) {
        await compileJavaFiles(entryPath);
      } else if (stats.isFile() && path.extname(entry) === '.java') {
        const relativePath = path.relative(SRC_DIR, dir);
        const packageStructure = relativePath ? `${relativePath.replace(/\//g, '.')}.` : '';
        const className = path.basename(entry, '.java');
        const classPath = path.join(BIN_DIR, packageStructure, `${className}.class`);

        await compileJavaFile(entryPath, classPath);
      }
    }
  } catch (error) {
    console.error('Error while compiling Java files:', error);
  }
}

function compileJavaFile(inputPath, outputPath) {
  return new Promise((resolve, reject) => {
    const javacCommand = `javac -cp "${SRC_DIR}" -d "${BIN_DIR}" "${inputPath}"`;

    exec(javacCommand, (error, stdout, stderr) => {
      if (error) {
        console.error(`Error compiling ${inputPath}:`, error.message);
        reject(error);
      } else {
        console.log(`Compiled ${inputPath}`);
        resolve();
      }
    });
  });
}

async function cleanBinDir(dir) {
  try {
    const entries = await fs.promises.readdir(dir);

    for (const entry of entries) {
      const entryPath = path.join(dir, entry);
      const stats = await fs.promises.lstat(entryPath);

      if (stats.isDirectory()) {
        await cleanBinDir(entryPath);
      } else {
        await fs.promises.unlink(entryPath);
      }
    }

    await fs.promises.rmdir(dir);
    console.log('Cleaned the "bin" directory.');
  } catch (error) {
    console.error('Error cleaning "bin" directory:', error);
  }
}

async function main() {
  try {
    // Create the 'bin' directory if it doesn't exist
    await mkdirIfNotExists(BIN_DIR);

    // Clean the 'bin' directory before compilation
    await cleanBinDir(BIN_DIR);

    // Start compilation from the 'src' folder
    await compileJavaFiles(SRC_DIR);

    console.log('Java files compilation completed!');
  } catch (error) {
    console.error('Error:', error);
  }
}

function mkdirIfNotExists(dir) {
  return new Promise((resolve, reject) => {
    fs.mkdir(dir, { recursive: true }, (error) => {
      if (error) {
        reject(error);
      } else {
        resolve();
      }
    });
  });
}

main();
