BEGIN TRANSACTION;

DROP TABLE IF EXISTS tenmo_user, account, transaction;

DROP SEQUENCE IF EXISTS seq_user_id, seq_account_id;

-- Sequence to start user_id values at 1001 instead of 1
CREATE SEQUENCE seq_user_id
  INCREMENT BY 1
  START WITH 1001
  NO MAXVALUE;

CREATE TABLE tenmo_user (
	user_id int NOT NULL DEFAULT nextval('seq_user_id'),
	username varchar(50) NOT NULL,
	password_hash varchar(200) NOT NULL,
	CONSTRAINT PK_tenmo_user PRIMARY KEY (user_id),
	CONSTRAINT UQ_username UNIQUE (username)
);

-- Sequence to start account_id values at 2001 instead of 1
-- Note: Use similar sequences with unique starting values for additional tables
CREATE SEQUENCE seq_account_id
  INCREMENT BY 1
  START WITH 2001
  NO MAXVALUE;

CREATE TABLE account (
	account_id int NOT NULL DEFAULT nextval('seq_account_id'),
	user_id int NOT NULL,
	balance decimal(13, 2) NOT NULL,
	CONSTRAINT PK_account PRIMARY KEY (account_id),
	CONSTRAINT FK_account_tenmo_user FOREIGN KEY (user_id) REFERENCES tenmo_user (user_id)
);

CREATE TABLE transaction (
	transaction_id serial,
	sender_id int NOT NULL,
	recipient_id int NOT NULL CHECK(sender_id != recipient_id),
	amount decimal(13,2) CHECK(amount > 0.00),
	status varchar(10) CHECK(status = 'Approved' OR status = 'Rejected' OR status = 'Pending'),
	is_request boolean NOT NULL,
	CONSTRAINT FK_sender_id FOREIGN KEY (sender_id) REFERENCES account(account_id),
	CONSTRAINT FK_recipient_id FOREIGN KEY (recipient_id) REFERENCES account(account_id)	
);


COMMIT;

-- INSERT INTO account
-- (account_id,user_id,)

-- INSERT INTO transaction
-- (sender_id, recipient_id, amount, status)
-- VALUES (1,2,54,'Rejected');

-- SELECT * FROM transaction;
