(() => {
    const magnitud = document.getElementById("Magnitud");
    const origen = document.getElementById("UnidadOrigen");
    const destino = document.getElementById("UnidadDestino");
    if (!magnitud || !origen || !destino) return;

    const filtrar = (lista) => {
        for (const opcion of lista.options) {
            if (!opcion.dataset.magnitud) continue;
            opcion.hidden = opcion.dataset.magnitud !== magnitud.value;
        }
        if (lista.selectedOptions[0]?.hidden) {
            lista.value = [...lista.options].find(opcion => opcion.dataset.magnitud === magnitud.value)?.value ?? "";
        }
    };

    const actualizar = () => {
        filtrar(origen);
        filtrar(destino);
    };
    magnitud.addEventListener("change", actualizar);
    actualizar();
})();
