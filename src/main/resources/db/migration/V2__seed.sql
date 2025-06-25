INSERT INTO card (name, limit_amount, closing_day, payment_day) VALUES
('Nubank', 5050, 10, 17 ),
('Mercado Pago', 5800, 15, 22);

INSERT INTO invoice (card_id, amount, closing_date, payment_date, is_paid) VALUES
(1, 0, '2025-07-10', '2025-07-17', FALSE),
(2, 0, '2025-07-15', '2025-07-22', FALSE);