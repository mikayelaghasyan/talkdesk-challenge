print('Initialising call db');

db = db.getSiblingDB('call-db');
db.createUser(
  {
    user: 'call',
    pwd: 'call',
    roles: [{ role: 'readWrite', db: 'call-db' }],
  },
);
db.createCollection('call');

print('call db initialised');