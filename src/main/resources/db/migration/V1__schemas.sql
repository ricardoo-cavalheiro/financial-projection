CREATE TABLE card (
  id            SERIAL   PRIMARY KEY,
  name          TEXT     NOT NULL,
  limit_amount  INT      NOT NULL,
  closing_day   SMALLINT NOT NULL CHECK (closing_day > 0 AND closing_day <= 31),
  payment_day   SMALLINT NOT NULL CHECK (payment_day > 0 AND payment_day <= 31)
);

CREATE TABLE invoice (
  id              SERIAL        PRIMARY KEY,
  card_id         INT           NOT NULL REFERENCES card(id) ON DELETE CASCADE,
  amount          NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (amount >= 0),
  closing_date    DATE          NOT NULL,
  payment_date    DATE          NOT NULL,
  is_paid         BOOLEAN       NOT NULL DEFAULT FALSE,
  created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TYPE expense_kind AS ENUM ('CREDIT', 'DEBIT');

CREATE TABLE expense (
  id                  SERIAL        PRIMARY KEY,
  description         TEXT          NOT NULL,
  amount              NUMERIC(10,2) NOT NULL CHECK (amount >= 0),
  payment_type        expense_kind  NOT NULL,
  is_recurring        BOOLEAN       NOT NULL DEFAULT FALSE,
  is_paid             BOOLEAN       NOT NULL DEFAULT FALSE,
  invoice_id          INT           NULL REFERENCES invoice(id) ON DELETE SET NULL,
  card_id             INT           NULL REFERENCES card(id) ON DELETE SET NULL,
  total_installments  SMALLINT      NULL, -- quantidade total de parcelas caso o gasto tenha sido parcelado
  installment_number  SMALLINT      NULL, -- número da parcela atual
  payment_date        DATE          NOT NULL,
  created_at          TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE income (
  id               SERIAL         PRIMARY KEY,
  description      TEXT           NOT NULL,
  amount           NUMERIC(12,2)  NOT NULL CHECK (amount >= 0),
  payment_date     TIMESTAMP      NULL,
  recurrence_day   SMALLINT       NULL, -- dia do mês em que a entrada acontece
  is_recurring     BOOLEAN        NOT NULL DEFAULT FALSE,
  created_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);