import json

from database import Database
from permission_old import Permission


class Utils:
    """实用类"""
    db = None

    def __init__(self, db: Database):
        self.db = db

    def check_token(self, token: str) -> bool:
        if self.db.get(token=token) is None:
            return False
        else:
            return True

    def get_token(self, guild: str) -> str:
        return self.db.get(guild=guild)['token']

    def check_guild_token_exists(self, guild: str) -> bool:
        if self.db.get(guild=guild) is None:
            return False
        else:
            return True

    def set_token(self, token: str, guild: str):
        data = self.db.get(token=token)
        self.db.update(data['_id'], {'guild': guild})

    def get_perm(self, guild: str) -> Permission:
        return Permission(self.db.get(guild=guild)['permission'])

    def unset_token(self, guild: str):
        old_data = self.db.get(guild=guild)
        with open('database_example.json', 'r', encoding='utf8') as f:
            new_data = json.loads(f.read())
        new_data['token'] = old_data['token']
        self.db.update(old_data['_id'], new_data)

    def check_function_exists(self, function: str) -> bool:
        if function.lower() not in ['log', 'chat', 'player_command', 'login', 'logout', 'rcon_command']:
            return False
        return True

    def check_function_status(self, function: str, token: str = None, guild: str = None) -> bool:
        if not self.check_function_exists(function):
            return False
        func = function.lower()
        if token is not None:
            data = self.db.get(token=token)
        elif guild is not None:
            data = self.db.get(guild=guild)
        else:
            return False
        if data['channel'][func] == '-1':
            return False
        else:
            return True

    def set_channel(self, guild: str, channel: str, function: str) -> bool:
        if not self.check_function_exists(function) and function != 'default':
            return False
        func = function.lower()
        data = self.db.get(guild=guild)
        data['channel'][func] = channel
        self.db.update(data['_id'], {'channel': data['channel']})
        return True

    def get_channel(self, token: str, function: str) -> str:
        if not self.check_function_exists(function):
            return '-1'
        func = function.lower()
        data = self.db.get(token=token)
        if data['channel'][func] == '0':
            return data['channel']['default']
        return data['channel'][func]

    def add_filter(self, guild: str, _filter: str) -> bool:
        data = self.db.get(guild=guild)
        _filter = _filter.lower()
        if _filter in data['filter']:
            return False
        else:
            data['filter'].append(_filter)
        self.db.update(data['_id'], {'filter': data['filter']})
        return True

    def get_filter(self, guild: str) -> list:
        data = self.db.get(guild=guild)
        return data['filter']

    def check_filter_in_text(self, token: str, text: str) -> bool:
        data = self.db.get(token=token)
        text = text.lower()
        for i in data['filter']:
            if text.find(i) > -1:
                return True
        return False
