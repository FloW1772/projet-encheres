document.addEventListener("DOMContentLoaded", function () {
  const radios = document.querySelectorAll('input[name="typeFiltre"]');
  let lastCheckedRadio = null;

  radios.forEach((radio) => {
    radio.addEventListener("click", function (e) {
      if (lastCheckedRadio === radio) {
        // décocher radio au 2e clic
        e.preventDefault();  // empêche la sélection normale
        radio.checked = false;
        lastCheckedRadio = null;
        // Désactive tous les filtres
        toggleFiltres("achat", false);
        toggleFiltres("vente", false);
      } else {
        lastCheckedRadio = radio;

        if (radio.value === "reset") {
          // Si reset, décocher tout
          toggleFiltres("achat", false);
          toggleFiltres("vente", false);
          radio.checked = true;
        } else {
          // Active filtres liés au choix
          toggleFiltres(radio.value, true);
          // Désactive les autres filtres
          toggleFiltres(radio.value === "achat" ? "vente" : "achat", false);
        }
      }
    });
  });

  function toggleFiltres(type, enable) {
    const checkboxes = document.querySelectorAll(`#filtres${capitalize(type)} input[type="checkbox"]`);
    checkboxes.forEach((cb) => {
      cb.disabled = !enable;
      if (!enable) cb.checked = false;
    });
  }

  function capitalize(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
  }

  // Initialisation au chargement
  const checked = [...radios].find(r => r.checked);
  if (checked) {
    lastCheckedRadio = checked;
    if (checked.value === "reset") {
      toggleFiltres("achat", false);
      toggleFiltres("vente", false);
    } else {
      toggleFiltres(checked.value, true);
      toggleFiltres(checked.value === "achat" ? "vente" : "achat", false);
    }
  } else {
    toggleFiltres("achat", false);
    toggleFiltres("vente", false);
  }
});
