(() => {
    const magnitud = document.getElementById("Magnitud");
    const origen = document.getElementById("UnidadOrigen");
    const destino = document.getElementById("UnidadDestino");
    if (!magnitud || !origen || !destino) return;
    const formulario = document.getElementById("formulario-conversion");
    const valor = document.getElementById("Valor");

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
    valor?.addEventListener("input", () => valor.setCustomValidity(""));
    formulario?.addEventListener("submit", evento => {
        const texto = valor.value.trim();
        const numero = Number(texto);
        if (texto === "" || !Number.isFinite(numero)
            || (magnitud.value.toLowerCase() === "masa" && numero < 0)) {
            evento.preventDefault();
            valor.setCustomValidity(magnitud.value.toLowerCase() === "masa" && numero < 0
                ? "La masa no puede ser negativa."
                : "Ingrese un número finito.");
            valor.reportValidity();
        }
    });
})();
