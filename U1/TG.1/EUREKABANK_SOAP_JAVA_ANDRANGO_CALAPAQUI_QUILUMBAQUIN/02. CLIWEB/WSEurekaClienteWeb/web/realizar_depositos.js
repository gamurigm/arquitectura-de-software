$(document).ready(function() {
    $('#depositoForm').submit(function(event) {
        event.preventDefault();

        var cuenta = $('#cuentaInput').val();
        var importe = parseFloat($('#importeInput').val());

        var url = 'http://localhost:8080/WS_EurekaBank/webresources/coreBancario/deposito';

        // Datos a enviar en formato x-www-form-urlencoded
        var data = {
            cuenta: cuenta,
            importe: importe
        };

        $.ajax({
            type: 'POST',
            url: url,
            data: data,
            success: function(response) {
                $('#resultado').html('<div class="alert alert-success">Depósito realizado correctamente.</div>');
                $('#depositoForm')[0].reset(); // Limpiar el formulario después del éxito
            },
            error: function(xhr, status, error) {
                $('#resultado').html('<div class="alert alert-danger">Error al realizar el depósito: ' + error + '</div>');
            }
        });
    });
});
