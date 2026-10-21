USE eurekabank_gr09;

UPDATE Credenciales
SET contrasena = 'pbkdf2-sha256$600000$5cV5Rt7hgFTlKOdg0I25uA$XVGksBT2fPaLTMjHEbMEIiOa-2o8LU9dEzEFcUCEj7o'
WHERE LOWER(usuario) = 'monster';
