let groups = JSON.parse(localStorage.getItem("gravity_groups")) || [];

// Groups used to be addressed by array index (group.html?id=<index>), which
// silently pointed at the wrong group as soon as an earlier group was
// deleted - any group after it shifted down and inherited its old index.
// Give every group a stable id, backfilling any older saved data that
// doesn't have one yet.
let needsMigration = false;
groups.forEach((group) => {
  if (!group.id) {
    group.id = crypto.randomUUID();
    needsMigration = true;
  }
});
if (needsMigration) {
  localStorage.setItem("gravity_groups", JSON.stringify(groups));
}

const dashboard = document.getElementById("hub-dashboard");
const modal = document.getElementById("group-modal");

function renderGroups() {
  dashboard.innerHTML = "";
  groups.forEach((group, index) => {
    const card = document.createElement("div");
    card.className = "group-card";
    card.style.position = "relative"; // Crucial for positioning delete btn

    card.innerHTML = `
            <button class="delete-btn" title="Delete Group" style="position: absolute; top: 10px; right: 10px;">×</button>
            <button class="edit-btn" title="Rename Group" style="position: absolute; top: 10px; left: 10px;">✎</button>
            <h2>${group.name}</h2>
            <p>Firmware: ${group.type}</p>
            <p>Printers: ${group.printers ? group.printers.length : 0}</p>
        `;

    // Card Click: Enter Group
    card.onclick = () => (window.location.href = `group.html?id=${group.id}`);

    // Delete Logic
    const delBtn = card.querySelector(".delete-btn");
    delBtn.onclick = (e) => {
      e.stopPropagation(); // Stop from opening the group
      if (confirm(`Delete the entire group "${group.name}"?`)) {
        groups.splice(index, 1);
        saveAndRender();
      }
    };

    // Edit/Rename Logic
    const editBtn = card.querySelector(".edit-btn");
    editBtn.onclick = (e) => {
      e.stopPropagation();
      const newName = prompt("Enter new group name:", group.name);
      if (newName && newName.trim() !== "") {
        group.name = newName.trim();
        saveAndRender();
      }
    };

    dashboard.appendChild(card);
  });
}

function saveAndRender() {
  localStorage.setItem("gravity_groups", JSON.stringify(groups));
  renderGroups();
}

// Modal Controls
document.getElementById("add-group-btn").onclick = () =>
  (modal.style.display = "block");
document.getElementById("close-modal-btn").onclick = () =>
  (modal.style.display = "none");

document.getElementById("save-group-btn").onclick = () => {
  const nameInput = document.getElementById("group-name-input");
  const typeInput = document.getElementById("group-type-input");

  if (nameInput.value) {
    groups.push({
      id: crypto.randomUUID(),
      name: nameInput.value,
      type: typeInput.value,
      printers: [],
    });
    saveAndRender();
    modal.style.display = "none";
    nameInput.value = "";
  }
};

renderGroups();
