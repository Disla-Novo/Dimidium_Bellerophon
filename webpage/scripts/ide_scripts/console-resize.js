// this program resizes the console section of the webpage
// it was a bit of a pain but it works the way it's meant to. it should save to local storage so it remains persistant
// helps with exception log clutter that i witnessed during development.

// no longer saved via localstorage

document.addEventListener("DOMContentLoaded", function () {
  const consoleHeader = document.querySelector(".console-header");
  const workspace = document.querySelector(".workspace");
  const consoleSection = document.querySelector(".console");
  const consoleLog = document.getElementById("console-log");

  if (!consoleHeader || !workspace || !consoleSection || !consoleLog) return;

  let isResizing = false;
  let startY, startHeight;

  const MIN_HEIGHT = 100;
  const MAX_HEIGHT = 600;
  const DEFAULT_HEIGHT = 150;
  //const STORAGE_KEY = "jupitore-console-height";

  /* Initialize console height
  const savedHeight = parseInt(localStorage.getItem(STORAGE_KEY), 10);
  setConsoleHeight(!isNaN(savedHeight) ? savedHeight : DEFAULT_HEIGHT);
*/
  // Initialize console height
  const savedHeight = Persistence.get("ui.consoleHeight", DEFAULT_HEIGHT);
  setConsoleHeight(savedHeight);

  function setConsoleHeight(height) {
    height = Math.max(MIN_HEIGHT, Math.min(MAX_HEIGHT, height));
    consoleSection.style.height = height + "px";
    workspace.style.gridTemplateRows = `50px 1fr ${height}px`;
    consoleLog.style.height = `calc(${height}px - ${consoleHeader.offsetHeight}px)`;
    Persistence.set("ui.consoleHeight", height);
  }

  consoleHeader.addEventListener("mousedown", function (e) {
    isResizing = true;
    startY = e.clientY;
    startHeight = consoleSection.offsetHeight;
    e.preventDefault();
    document.body.style.cursor = "ns-resize";
    document.body.style.userSelect = "none";
  });

  // main dogs
  document.addEventListener("mousemove", function (e) {
    if (!isResizing) return;
    const dy = startY - e.clientY;
    setConsoleHeight(startHeight + dy);
  });

  document.addEventListener("mouseup", function () {
    if (!isResizing) return;
    isResizing = false;
    document.body.style.cursor = "";
    document.body.style.userSelect = "";
  });

  window.addEventListener("resize", function () {
    setConsoleHeight(consoleSection.offsetHeight);
  });
});
