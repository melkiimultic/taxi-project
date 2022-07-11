CREATE index order_status_index
ON orders(status);

CREATE index driver_index
ON orders(driver);