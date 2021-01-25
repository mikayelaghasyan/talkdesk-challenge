print('Initialising stat db');

db = db.getSiblingDB('stat-db');
db.createUser(
  {
    user: 'stat',
    pwd: 'stat',
    roles: [{ role: 'readWrite', db: 'stat-db' }],
  },
);
db.createCollection('stat');
db.createCollection('call-snapshot');

print('stat db initialised');