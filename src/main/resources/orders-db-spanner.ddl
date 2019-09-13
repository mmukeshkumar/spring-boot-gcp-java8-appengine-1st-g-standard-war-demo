CREATE TABLE Orders (
	order_id STRING(36) NOT NULL,
	customer_id STRING(128) NOT NULL,
	first_name STRING(128) NOT NULL,
	last_name STRING(128) NOT NULL,
	order_date DATE NOT NULL
) PRIMARY KEY (order_id);
CREATE INDEX OrdersByCustomerId ON Orders(customer_id);
