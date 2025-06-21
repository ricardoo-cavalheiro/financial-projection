CREATE TABLE card (
  id          SERIAL PRIMARY KEY,
  name        TEXT NOT NULL,
  limit_amount       INT NOT NULL,
  closing_day SMALLINT NOT NULL CHECK (closing_day BETWEEN 1 AND 31),
  payment_day SMALLINT NOT NULL CHECK (payment_day BETWEEN 1 AND 31)
);

CREATE TYPE expense_kind AS ENUM ('CREDIT', 'DEBIT');

CREATE TABLE expense (
  id            SERIAL PRIMARY KEY,
  description   TEXT        NOT NULL,
  amount        NUMERIC(10,2) NOT NULL CHECK (amount >= 0),
  payment_type          expense_kind NOT NULL,
  is_recurring  BOOLEAN     NOT NULL DEFAULT FALSE,
  recurrence_day SMALLINT NULL, -- dia do mês em que o gasto acontece
  card_id       INT         NULL REFERENCES card(id) ON DELETE SET NULL,
  installments SMALLINT NULL, -- quantidade total de parcelas caso o gasto tenha sido parcelado
  current_installment SMALLINT NULL, -- número da parcela atual
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE income (
  id               SERIAL PRIMARY KEY,
  description      TEXT           NOT NULL,
  amount           NUMERIC(12,2)  NOT NULL CHECK (amount >= 0),
  recurrence_day   SMALLINT           NOT NULL, -- dia do mês em que a entrada acontece
  created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);