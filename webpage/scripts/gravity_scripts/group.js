// Data Retrieval
const urlParams = new URLSearchParams(window.location.search);
const groupId = urlParams.get("id");
let groups = JSON.parse(localStorage.getItem("gravity_groups")) || [];
let currentGroup = groups.find((group) => group.id === groupId);

// Redirect if something goes wrong
if (!currentGroup) {
  window.location.href = "hub.html";
}

function persistGroups() {
  const index = groups.findIndex((group) => group.id === groupId);
  groups[index] = currentGroup;
  localStorage.setItem("gravity_groups", JSON.stringify(groups));
}

// UI Elements
const title = document.getElementById("group-title");
const dashboard = document.getElementById("printer-dashboard");
const modal = document.getElementById("printer-modal");
const typeDisplay = document.getElementById("display-machine-type");

title.textContent = `${currentGroup.name} Dashboard`;
typeDisplay.value = currentGroup.type;

function renderPrinters() {
  dashboard.innerHTML = "";

  if (!currentGroup.printers) currentGroup.printers = [];

  currentGroup.printers.forEach((printer, index) => {
    const card = document.createElement("div");
    card.className = "printer-card";

    // Removed the IP address line from the innerHTML
    card.innerHTML = `
            <button class="delete-btn" title="Delete Printer">×</button>
            <div class="status-dot"></div>
            <h3>${printer.name}</h3>
            <p>Status: <span class="status-text">Ready</span></p>
        `;

    // It still knows the IP because it's captured in this closure
    card.onclick = () => {
      window.location.href = `dash.html?ip=${encodeURIComponent(printer.ip)}`;
    };

    const delBtn = card.querySelector(".delete-btn");
    delBtn.onclick = (e) => {
      e.stopPropagation();
      deletePrinter(index);
    };

    dashboard.appendChild(card);
  });
}

function deletePrinter(index) {
  if (confirm("Delete this printer?")) {
    currentGroup.printers.splice(index, 1); // Remove from array
    persistGroups(); // Update master list and save
    renderPrinters(); // Refresh UI
  }
}

// Modal Controls
document.getElementById("add-printer-btn").onclick = () => {
  modal.style.display = "block";
};

document.getElementById("close-printer-modal").onclick = () => {
  modal.style.display = "none";
};

// dash.html's own check (does this jam into a URL object?) turns out to
// accept almost anything, since the URL spec just percent-encodes garbage
// into the host instead of rejecting it - "not a valid ip!!" parses to
// http://not%20a%20valid%20ip!!/ without throwing. Actually validate that
// the host looks like an IPv4 address or a plausible hostname, with an
// optional :port and an optional http(s):// prefix.
function isValidPrinterAddress(value) {
  const withoutScheme = value.replace(/^https?:\/\//i, "");
  const hostPort = withoutScheme.split(/[/?#]/)[0];

  const match = hostPort.match(/^([^:]+)(?::(\d+))?$/);
  if (!match) return false;

  const [, host, port] = match;
  if (port !== undefined) {
    const portNum = Number(port);
    if (portNum < 1 || portNum > 65535) return false;
  }

  if (/^(\d{1,3}\.){3}\d{1,3}$/.test(host)) {
    return host.split(".").every((octet) => Number(octet) <= 255);
  }

  const hostnameLabel = "(?!-)[A-Za-z0-9-]{1,63}(?<!-)";
  const hostnamePattern = new RegExp(`^${hostnameLabel}(\\.${hostnameLabel})*$`);
  return hostnamePattern.test(host);
}

// Saving Logic
document.getElementById("save-printer-btn").onclick = () => {
  const pName = document.getElementById("printer-name-input").value.trim();
  const pIp = document.getElementById("printer-ip-input").value.trim();

  if (!pName || !pIp) {
    alert("Enter a name and IP.");
    return;
  }

  if (!isValidPrinterAddress(pIp)) {
    alert("Enter a valid IP address or URL (e.g. 192.168.1.50 or http://192.168.1.50).");
    return;
  }

  currentGroup.printers.push({ name: pName, ip: pIp });
  persistGroups();

  renderPrinters();

  modal.style.display = "none";
  document.getElementById("printer-name-input").value = "";
  document.getElementById("printer-ip-input").value = "";
};

renderPrinters();
