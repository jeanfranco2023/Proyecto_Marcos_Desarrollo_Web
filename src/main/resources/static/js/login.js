document.addEventListener('DOMContentLoaded', function () {
    const telefonoInput = document.getElementById('NumeroTelefono');
    const passwordInput = document.getElementById('passwordRegistro');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const formRegistro = document.getElementById('formRegistro');
    const errorModal = new bootstrap.Modal(document.getElementById('errorModal'));
    const errorModalMessage = document.getElementById('errorModalMessage');

    formRegistro.addEventListener('submit', function (e) {
        e.preventDefault(); // evitar envío por defecto

        const telefono = telefonoInput.value.trim();
        const password = passwordInput.value;
        const confirmPassword = confirmPasswordInput.value;

        // Validaciones
        if (!/^\d{9}$/.test(telefono)) {
            errorModalMessage.textContent = 'El número de teléfono debe tener exactamente 9 dígitos.';
            errorModal.show();
            return;
        }

        if (password.length < 12 || !/[A-Z]/.test(password)) {
            errorModalMessage.textContent = 'La contraseña debe tener al menos 12 caracteres e incluir una letra mayúscula.';
            errorModal.show();
            return;
        }

        if (password !== confirmPassword) {
            errorModalMessage.textContent = 'Las contraseñas no coinciden.';
            errorModal.show();
            return;
        }

        // Si pasa todas las validaciones, se puede enviar el formulario
        formRegistro.submit();
    });
});
