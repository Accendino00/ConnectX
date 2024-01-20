/*
    Execute this like (per vederlo mentre gioca): 
        node nodeexecute.js 6 7 4 -v
    Execute this like (per evitare di verderlo):
        node nodeexecute.js 6 7 4 -r 20
*/

const { exec } = require('child_process');
const path = require('path');

const M = process.argv[2];
const N = process.argv[3];
const K = process.argv[4];
const useCXGame = process.argv.includes('-v');
const inversePlayer = process.argv.includes('-i');
const repeatTimesIndex = process.argv.indexOf('-r');
const repeatTimes = repeatTimesIndex !== -1 ? process.argv[repeatTimesIndex + 1] : null;

const mainClass = useCXGame ? 'connectx.CXGame' : 'connectx.CXPlayerTester';

function getJavaExecutionCommand(className, args) {
  const classPath = path.join('bin', className.replace(/\./g, '/'));
  return `java -cp "${path.resolve('bin')}" ${className} ${M} ${N} ${K} ${args}`;
}

function executeJavaCommand(className, args) {
  const command = getJavaExecutionCommand(className, args);
  return new Promise((resolve, reject) => {
    exec(command, (error, stdout, stderr) => {
      if (error) {
        console.error(`Error executing Java command: ${command}`);
        reject(error);
      } else {
        console.log(`Java command executed: ${command}`);
        console.log(stdout);
        resolve();
      }
    });
  });
}

async function main() {
  try {
     //let additionalArgs = ` connectx.euristics_budspencer.BeatriceDiDante connectx.L1.L1`;
     // let additionalArgs = ` connectx.abp_budspencer.Rododendro connectx.sfidanti.Rebarbaro`;
     //let additionalArgs = ` connectx.MxLxPlayer.MxLxPlayer connectx.abp_budspencer.Rododendro`;
     // let additionalArgs = ` connectx.MxLxPlayer.MxLxPlayer connectx.abp_budspencer.Rododendrodos`;
     let additionalArgs = ` connectx.MxLxPlayer.MxLxPlayer connectx.mcts_budspencer.BudSpencer`;
     
     //   let additionalArgs = ` connectx.euristics_budspencer.HogRider connectx.euristics_budspencer.Leftist`;
     // let additionalArgs = ` connectx.euristics_budspencer.BeatriceDiDante connectx.abp_budspencer.Rododendro`;
     //let additionalArgs = ` connectx.L1.L1 connectx.euristics_budspencer.BeatriceDiDante`;
      // let additionalArgs = ` connectx.euristics_budspencer.GeronimoStilton connectx.euristics_budspencer.BeatriceDiDante`;
     // let additionalArgs = ` connectx.euristics_budspencer.BeatriceDiDante connectx.euristics_budspencer.GeronimoStilton`;
    //let additionalArgs = ` connectx.L1.L1 connectx.negamax_budspencer.BudSpencer`;
    // let additionalArgs = ` connectx.negamaxr_budspencer.BudSpencer connectx.negamax_budspencer.BudSpencer`;
    // let additionalArgs = ` connectx.negamax_budspencer.BudSpencer connectx.negamaxr_budspencer.BudSpencer`;
    
    if(inversePlayer) {
      // Invert the two players separated by space in the additionalArgs string
      const players = additionalArgs.split(' ');
      additionalArgs = ` ${players[2]} ${players[1]}`;
    }
    
    if (repeatTimes !== null) {
      additionalArgs += ` -r ${repeatTimes}`;
    }
    await executeJavaCommand(mainClass, additionalArgs);
  } catch (error) {
    console.error('Error:', error);
  }
}

main();

