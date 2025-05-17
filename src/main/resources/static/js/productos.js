document.addEventListener('DOMContentLoaded', () => {
    const colorOptions = document.querySelectorAll('.color-option');
    const sizeOptions = document.querySelectorAll('.size-option');
    const minusBtn = document.querySelector('.quantity-btn.minus');
    const plusBtn = document.querySelector('.quantity-btn.plus');
    const quantityInput = document.querySelector('.quantity-input');
    const addToCartBtn = document.querySelector('.add-to-cart');

    const inputColor = document.getElementById('inputColor');
    const inputTalla = document.getElementById('inputTalla');
    const inputCantidad = document.getElementById('inputCantidad');
    const form = document.getElementById('cartForm');

    // Inicializar valores por defecto
    const initialColor = document.querySelector('.color-option.selected')?.getAttribute('data-color');
    const initialSize = document.querySelector('.size-option.selected')?.textContent;
    if (initialColor) inputColor.value = initialColor;
    if (initialSize) inputTalla.value = initialSize;

    // Selección de color
    colorOptions.forEach(option => {
        option.addEventListener('click', () => {
            document.querySelector('.color-option.selected')?.classList.remove('selected');
            option.classList.add('selected');
            const selectedColor = option.getAttribute('data-color');
            inputColor.value = selectedColor;
        });
    });

    // Selección de talla
    sizeOptions.forEach(option => {
        option.addEventListener('click', () => {
            document.querySelector('.size-option.selected')?.classList.remove('selected');
            option.classList.add('selected');
            const selectedSize = option.textContent;
            inputTalla.value = selectedSize;
        });
    });

    // Botones de cantidad
    minusBtn.addEventListener('click', () => {
        let value = parseInt(quantityInput.value);
        if (value > 1) quantityInput.value = value - 1;
        inputCantidad.value = quantityInput.value;
    });

    plusBtn.addEventListener('click', () => {
        let value = parseInt(quantityInput.value);
        quantityInput.value = value + 1;
        inputCantidad.value = quantityInput.value;
    });

    quantityInput.addEventListener('change', () => {
        let value = parseInt(quantityInput.value);
        if (isNaN(value) || value < 1) value = 1;
        quantityInput.value = value;
        inputCantidad.value = value;
    });

    // Enviar formulario
    addToCartBtn.addEventListener('click', (e) => {
        const selectedColor = inputColor.value;
        const selectedSize = inputTalla.value;

        if (!selectedColor || !selectedSize) {
            e.preventDefault();
            alert("Por favor selecciona un color y una talla.");
            return;
        }

        // Opcional: animación visual
        addToCartBtn.textContent = 'Añadiendo...';
        addToCartBtn.disabled = true;

        // Enviar formulario después de un pequeño delay si deseas
        setTimeout(() => {
            form.submit(); // ← ¡Este es el envío real al backend!
        }, 300); // Puedes ajustar el tiempo o quitarlo
    });
});
