from config import Config
from database import Database
from permission_old import Permission

conf = Config('config.json')
db = Database(conf.db)
guild_or_token = input('请输入群组ID或token: ')
data = db.get(token=guild_or_token)
if data is None:
    data = db.get(guild=guild_or_token)
if data is None:
    print('该token不存在 或 群组未绑定token')
else:
    perm = Permission(data['permission'])
    user = input('请输入用户ID: ')
    permission = input('请输入权限名称: ')
    perm.add_user_permission(user, permission)
    db.update_permission(data['_id'], perm.get_dict())
