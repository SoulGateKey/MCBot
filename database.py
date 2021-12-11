import pymongo
from bson import ObjectId


class Database:
    """数据库类"""

    db = None
    db_token = None

    def __init__(self, url: str):
        db_clint = pymongo.MongoClient(url)
        self.db = db_clint['MCBot']
        self.db_token = db_clint['MCBot']['token']

    def get(self, token: str = None, guild: str = None):
        if token is not None:
            return self.db_token.find_one(
                {'token': token}
            )
        elif guild is not None:
            return self.db_token.find_one(
                {'guild': guild}
            )
        else:
            return None

    def update_all(self, data: dict):
        return self.db_token.update_one(
            {'_id': ObjectId(str(data['_id']))},
            {'$set': data}
        )

    def update(self, _id, data: dict):
        return self.db_token.update_one(
            {'_id': ObjectId(str(_id))},
            {'$set': data}
        )

    def update_permission(self, _id, data: dict):
        return self.db_token.update_one(
            {'_id': ObjectId(str(_id))},
            {'$set': {'permission': data}}
        )

    def insert_one(self, data: dict):
        return self.db_token.insert_one(data)
