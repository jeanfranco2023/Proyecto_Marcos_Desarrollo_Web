document.addEventListener('DOMContentLoaded', function () {
    function setupMobileMenu() {
        if (window.innerWidth < 992) {
            document.querySelectorAll('.first-level-item.has-submenu > a').forEach(function (link) {
                link.addEventListener('click', function (e) {
                    if (this.parentElement.classList.contains('has-submenu')) {
                        e.preventDefault();
                        document.querySelectorAll('.first-level-item').forEach(function (item) {
                            if (item !== this.parentElement) {
                                item.classList.remove('active');
                            }
                        }.bind(this));

                        this.parentElement.classList.toggle('active');
                    }
                });
            });

            document.querySelectorAll('.mega-menu-item a').forEach(function (link) {
                link.addEventListener('click', function (e) {
                });
            });

            document.querySelector('.nav-item.dropdown > a').addEventListener('click', function (e) {
                e.preventDefault();
                const menu = this.nextElementSibling;
                const isShowing = menu.classList.contains('show');

                document.querySelectorAll('.first-level-menu').forEach(function (m) {
                    m.classList.remove('show');
                });

                document.querySelectorAll('.first-level-item').forEach(function (item) {
                    item.classList.remove('active');
                });

                if (!isShowing) {
                    menu.classList.add('show');
                }
            });
        }
    }
    setupMobileMenu();
    window.addEventListener('resize', setupMobileMenu);

    // 1. Galería de imágenes por color
    const productImages = {
        azul: [
            '/img/camisas/camisa-cargo-de-color-solido1-az.jpg',
            '/img/camisas/camisa-cargo-de-color-solido2-az.jpg',
            '/img/camisas/camisa-cargo-de-color-solido3-az.jpg',
            '/img/camisas/camisa-cargo-de-color-solido4-az.jpg',
            '/img/camisas/camisa-cargo-de-color-solido5-az.jpg',
            '/img/camisas/camisa-cargo-de-color-solido6-az.jpg'
        ],
        marron: [
            '/img/camisas/camisa-cargo-de-color-solido1-Marron.jpg',
            '/img/camisas/camisa-cargo-de-color-solido2-Marron.jpg',
            '/img/camisas/camisa-cargo-de-color-solido3-Marron.jpg',
            '/img/camisas/camisa-cargo-de-color-solido4-Marron.jpg',
            '/img/camisas/camisa-cargo-de-color-solido5-Marron.jpg',
            '/img/camisas/camisa-cargo-de-color-solido6-Marron.jpg'
        ],
        negro: [
            '/img/camisas/camisa-cargo-de-color-solido3-negro.jpg',
            '/img/camisas/camisa-cargo-de-color-solido2-negro.jpg',
            '/img/camisas/camisa-cargo-de-color-solido3-negro.jpg',
            '/img/camisas/camisa-cargo-de-color-solido4-negro.jpg',
            '/img/camisas/camisa-cargo-de-color-solido5-negro.jpg',
            '/img/camisas/camisa-cargo-de-color-solido6-negro.jpg'
        ],
        verde: [
            '/img/camisas/camisa-cargo-de-color-solido.jpg',
            '/img/camisas/camisa-cargo-de-color-solido2.jpg',
            '/img/camisas/camisa-cargo-de-color-solido3-v.jpg',
            '/img/camisas/camisa-cargo-de-color-solido4-v.jpg',
            '/img/camisas/camisa-cargo-de-color-solido5-v.jpg',
            '/img/camisas/camisa-cargo-de-color-solido6-v.jpg'
        ]
    };

    const mainImage = document.getElementById('mainImage');
    const thumbnailContainer = document.querySelector('.thumbnail-container');
    const colorOptions = document.querySelectorAll('.color-option');
    const sizeOptions = document.querySelectorAll('.size-option');
    const minusBtn = document.querySelector('.quantity-btn:first-child');
    const plusBtn = document.querySelector('.quantity-btn:last-child');
    const quantityInput = document.querySelector('.quantity-input');
    const addToCartBtn = document.querySelector('.add-to-cart');
    const tabs = document.querySelectorAll('.tab');
    const loadMoreBtn = document.getElementById('loadMoreBtn');
    const reviewsContainer = document.querySelector('.reviews-grid');

    loadImagesForColor('azul');

    function loadImagesForColor(color) {
        const images = productImages[color];
        if (!images || images.length === 0) return;

        thumbnailContainer.innerHTML = '';
        mainImage.src = images[0];

        images.forEach((img, index) => {
            const thumbnail = document.createElement('img');
            thumbnail.src = img;
            thumbnail.className = 'thumbnail' + (index === 0 ? ' active' : '');
            thumbnail.alt = `Vista ${index + 1}`;

            thumbnail.addEventListener('click', function() {
                mainImage.src = img;
                document.querySelectorAll('.thumbnail').forEach(t => t.classList.remove('active'));
                this.classList.add('active');
            });

            thumbnail.addEventListener('mouseenter', function() {
                if (!this.classList.contains('active')) {
                    mainImage.style.opacity = '0.7';
                    setTimeout(() => {
                        mainImage.src = img;
                        mainImage.style.opacity = '1';
                    }, 150);
                }
            });

            thumbnailContainer.appendChild(thumbnail);
        });

        const thumbnails = document.querySelectorAll('.thumbnail');
        if (thumbnails.length > 0) {
            const thumbnailHeight = thumbnails[0].offsetHeight;
            const gap = 10;
            thumbnailContainer.style.maxHeight = `${(thumbnailHeight + gap) * 5}px`;
        }
    }

    colorOptions.forEach(option => {
        option.addEventListener('click', function() {
            const selected = document.querySelector('.color-option.selected');
            if (selected) selected.classList.remove('selected');
            this.classList.add('selected');
            const selectedColor = this.getAttribute('data-color');
            loadImagesForColor(selectedColor);
        });
    });

    sizeOptions.forEach(option => {
        option.addEventListener('click', function() {
            const selected = document.querySelector('.size-option.selected');
            if (selected) selected.classList.remove('selected');
            this.classList.add('selected');
        });
    });

    minusBtn.addEventListener('click', function() {
        let value = parseInt(quantityInput.value);
        if (value > 1) {
            quantityInput.value = value - 1;
        }
    });

    plusBtn.addEventListener('click', function() {
        let value = parseInt(quantityInput.value);
        quantityInput.value = value + 1;
    });

    quantityInput.addEventListener('change', function() {
        let value = parseInt(this.value);
        if (isNaN(value) || value < 1) {
            this.value = 1;
        }
    });

    tabs.forEach(tab => {
        tab.addEventListener('click', function() {
            if (this.classList.contains('active')) return;
            document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
            document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
            this.classList.add('active');
            const tabId = this.getAttribute('data-tab');
            document.getElementById(tabId).classList.add('active');
        });
    });

    addToCartBtn.addEventListener('click', function() {
        const selectedColor = document.querySelector('.color-option.selected')?.getAttribute('data-color');
        const selectedSize = document.querySelector('.size-option.selected')?.textContent;
        const quantity = quantityInput.value;

        const originalText = this.textContent;
        this.textContent = 'Añadiendo...';
        this.disabled = true;

        setTimeout(() => {
            this.textContent = '✓ Añadido';
            setTimeout(() => {
                this.textContent = originalText;
                this.disabled = false;
            }, 2000);
        }, 500);

        console.log('Producto añadido:', {
            color: selectedColor,
            talla: selectedSize,
            cantidad: quantity
        });
    });

    if (loadMoreBtn && reviewsContainer) {
        let showingAll = false;
        const additionalReviews = [
            {
                author: "Laura G.",
                date: "Posted on August 20, 2023",
                rating: "★★★★★",
                text: "Excelente calidad, superó mis expectativas. Muy cómoda y los bolsillos son muy útiles."
            },
            {
                author: "Diego R.",
                date: "Posted on August 22, 2023",
                rating: "★★★★☆",
                text: "Buena relación calidad-precio. El color es exactamente como en las fotos."
            }
        ];

        loadMoreBtn.addEventListener('click', function() {
            if (showingAll) {
                const allReviews = Array.from(document.querySelectorAll('.review'));
                if (allReviews.length > 6) {
                    for (let i = allReviews.length - 1; i >= 6; i--) {
                        allReviews[i].remove();
                    }
                }
                this.textContent = 'Ver mas reseñas';
            } else {
                additionalReviews.forEach(review => {
                    const reviewElement = document.createElement('div');
                    reviewElement.className = 'review';
                    reviewElement.innerHTML = `
                        <div class="review-header">
                            <div>
                                <span class="review-author">${review.author}</span>
                                <div class="review-rating">${review.rating}</div>
                            </div>
                            <span class="review-date">${review.date}</span>
                        </div>
                        <p>${review.text}</p>
                    `;
                    reviewsContainer.appendChild(reviewElement);
                });
                this.textContent = 'Ver menos reseñas';
            }
            showingAll = !showingAll;
        });
    }

    document.querySelectorAll('.faq-item h3').forEach(question => {
        question.addEventListener('click', () => {
            const answer = question.nextElementSibling;
            answer.style.display = answer.style.display === 'none' ? 'block' : 'none';
            question.classList.toggle('active');
        });
    });

    const emailInput = document.querySelector('.email-input');
    const subscribeBtn = document.querySelector('.subscribe-btn');

    if (emailInput && subscribeBtn) {
        emailInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                subscribeToNewsletter();
            }
        });

        subscribeBtn.addEventListener('click', subscribeToNewsletter);

        function subscribeToNewsletter() {
            const email = emailInput.value.trim();
            if (email && isValidEmail(email)) {
                alert(`Gracias por suscribirte con el email: ${email}`);
                emailInput.value = '';
            } else {
                alert('Por favor ingresa un email válido');
            }
        }

        function isValidEmail(email) {
            return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
        }
    }

    document.querySelector('.tab[data-tab="reviews"]').classList.add('active');
    document.getElementById('reviews').classList.add('active');
});
