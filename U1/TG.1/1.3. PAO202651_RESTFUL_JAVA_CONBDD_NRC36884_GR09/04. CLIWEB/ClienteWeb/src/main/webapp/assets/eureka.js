const origen = document.querySelector('#cuenta-origen');
const destino = document.querySelector('#cuenta-destino');
if (origen && destino) {
    const todas = [...destino.options].slice(1).map(option => ({ value: option.value, text: option.text, moneda: option.dataset.currency }));
    const poblarDestinos = () => {
        const moneda = origen.selectedOptions[0]?.dataset.currency;
        destino.replaceChildren(new Option('Seleccione cuenta de destino', ''));
        todas.filter(c => c.value && c.value !== origen.value && c.moneda === moneda)
            .forEach(c => destino.add(new Option(c.text, c.value)));
        destino.disabled = destino.options.length === 1;
        const seleccion = origen.selectedOptions[0];
        const importe = document.querySelector('#importe-transferencia');
        const resumen = document.querySelector('#resumen-transferencia');
        if (importe && seleccion) importe.dataset.simbolo = seleccion.textContent.split(' · ').slice(-1)[0].trim().split(' ')[0];
        if (resumen) resumen.textContent = destino.disabled ? 'No tienes otra cuenta activa en la misma moneda.' : 'Elige una cuenta de destino para continuar.';
    };
    origen.addEventListener('change', poblarDestinos);
    destino.addEventListener('change', () => {
        const desde = origen.selectedOptions[0]?.textContent.trim() || '';
        const hacia = destino.selectedOptions[0]?.textContent.trim() || '';
        const resumen = document.querySelector('#resumen-transferencia');
        if (resumen) resumen.textContent = destino.value ? `Desde ${desde.split(' · ')[0]} hacia ${hacia.split(' · ')[0]}` : 'Elige una cuenta de destino para continuar.';
    });
    poblarDestinos();
}
