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
  is_paid         BOOLEAN     NOT NULL DEFAULT FALSE,
  card_id       INT         NULL REFERENCES card(id) ON DELETE SET NULL,
  total_installments SMALLINT NULL, -- quantidade total de parcelas caso o gasto tenha sido parcelado
  installment_number SMALLINT NULL, -- número da parcela atual
  payment_date      DATE        NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE income (
  id               SERIAL PRIMARY KEY,
  description      TEXT           NOT NULL,
  amount           NUMERIC(12,2)  NOT NULL CHECK (amount >= 0),
  recurrence_day   SMALLINT           NOT NULL, -- dia do mês em que a entrada acontece
  created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);