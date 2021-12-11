from config import Config
from database import Database
import json
import uuid

from utils import Utils

conf = Config('config.json')
db = Database(conf.db)
utils = Utils(db)
with open('database_example.json', 'r', encoding='utf8') as f:
    data = json.loads(f.read())
while True:
    token = str(uuid.uuid4())
    if not utils.check_token(token):
        break
data['token'] = token
db.insert_one(data)
print(data['token'])
