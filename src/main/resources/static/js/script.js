function afficherFiltres() {
	const radios = document.querySelectorAll('input[name="typeFiltre"]');
	let typeFiltre = null; radios.forEach(radio => { if (radio.checked) { typeFiltre = radio.value; } });
	document.getElementById('filtresAchat').style.display = (typeFiltre === 'achat') ? 'block' : 'none';
	document.getElementById('filtresVente').style.display = (typeFiltre === 'vente') ? 'block' : 'none';
}
window.addEventListener('DOMContentLoaded', () => { afficherFiltres(); document.querySelectorAll('input[name="typeFiltre"]').forEach(radio => { radio.addEventListener('change', afficherFiltres); }); });