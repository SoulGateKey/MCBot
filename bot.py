from khl import Bot, Message
from config import Config
from database import Database
from serverUtils import get_all_status
from utils import Utils
import logging


# logging.basicConfig(level='INFO')


class BotMain:
    """机器人类"""
    conf = None
    db = None
    bot = None

    def __init__(self, conf: Config, db: Database):
        self.conf = conf
        self.db = db

    def init(self):
        self.bot = Bot(token=self.conf.token)

    def register_default_function(self):
        utils = Utils(self.db)

        async def reply(msg: Message, text: str):
            if msg.ctx.guild.id not in self.conf.limit_guild or msg.ctx.channel.id in self.conf.limit_guild[
                msg.ctx.guild.id]:
                await msg.reply(text)

        @self.bot.command(name='hello', prefixes=self.conf.cmd_prefix)
        async def hello(msg: Message, *args):
            await reply(msg, f'GuildId: {msg.ctx.guild.id}\nChannelId: {msg.ctx.channel.id}\nAuthorId: {msg.author_id}')

        @self.bot.command(name='getuserid', prefixes=self.conf.cmd_prefix)
        async def getuserid(msg: Message, *args):
            if len(args) == 0:
                await reply(msg, f'{msg.author.username}: {msg.author_id}')
            else:
                if len(msg.extra['mention']) == 0:
                    await reply(msg, '没有at到用户')
                else:
                    message = ''
                    for user in args:
                        user = user.replace('#', ': ').replace('@', '\n')
                        message += user
                    message = message[1:]
                    if len(msg.extra['mention']) != len(args):
                        message += '\nPS: 可能有用户没at到'
                    await reply(msg, message)

        @self.bot.command(name='listrole', prefixes=self.conf.cmd_prefix)
        async def listrole(msg: Message, *args):
            message = ''
            role_data = await self.get_role_list(msg.ctx.guild.id)
            for i in role_data:
                message += f'{i["name"]}: {i["role_id"]}\n'
            await reply(msg, message)

        @self.bot.command(name='help', prefixes=self.conf.cmd_prefix)
        async def help(msg: Message, *args):
            cmd_prefix = ''
            for cp in self.conf.cmd_prefix:
                cmd_prefix += f'{cp} '
            cmd_prefix = cmd_prefix[:-1]
            message = '帮助:\n' \
                      f'支持{cmd_prefix}作为指令前缀\n' \
                      'PS: 以下指令均未添加前缀, 复制时请勿复制冒号\n' \
                      ' - hello: 获取服务器ID, 频道ID, 用户ID\n' \
                      ' - getuserid: 获得自己 或 at到的用户 的ID\n' \
                      ' - listrole: 列出当前频道角色ID\n' \
                      ' - settoken <token>: 设置服务器的token\n' \
                      ' - unsettoken: 解绑token\n' \
                      ' - setchannel:\n' \
                      '    - setchannel default: 设置默认频道\n' \
                      '    - setchannel reset: 恢复所有功能频道至默认\n' \
                      '    - setchannel <功能名称>: 设置单个功能频道\n' \
                      ' - info: 列出各功能启用情况, 对应频道ID(若为默认频道则不显示频道ID), tellraw格式\n' \
                      ' - function <功能名称> true/false: 设置功能开关(true启用, false关闭)\n' \
                      ' - status: 列出MC服务器在线情况, 版本, 在线玩家\n' \
                      ' - run <服务器名称> <指令>: 远程执行指令 (若指令内含有引号,请在引号前加 \ 进行反义; 原版指令无法获取返回,请开启日志转发功能)\n' \
                      ' - permission:\n' \
                      '    - permission list: 显示权限对应的角色名称及ID\n' \
                      '    - permission add <功能名称> <角色ID>: 给予角色对应权限\n' \
                      '    - permission del <功能名称> <角色ID>: 移除角色对应权限\n' \
                      ' - filter:\n' \
                      '    - filter list: 显示各功能的过滤关键词\n' \
                      '    - filter add <功能名称> <角色ID>: 添加该功能的过滤关键词\n' \
                      '    - filter del <功能名称> <角色ID>: 移除该功能的过滤关键词\n' \
                      ' - settellraw <json>: 设置开黑啦到服务器消息的tellraw格式, json需转义 (玩家ID: %playerId%, 消息内容: %text%)\n' \
                      ' - say <服务器名称> <消息内容>: 发送消息至服务器, 读取群组内昵称作为ID\n' \
                      '\n' \
                      '\n' \
                      '功能名称列表(status 与 command 通过权限设置):\n' \
                      ' - log: 日志转发功能\n' \
                      ' - chat: 聊天消息转发\n' \
                      ' - player_command: 玩家执行指令日志\n' \
                      ' - Login: 玩家登陆日志\n' \
                      ' - logout: 玩家退出日志\n' \
                      ' - rcon_command: Rcon指令指令日志\n' \
                      ' - command_return: 远程执行指令返回\n' \
                      ' - status: 服务器状态\n' \
                      ' - command: 远程执行指令\n'
            await reply(msg, message)

        @self.bot.command(name='settoken', prefixes=self.conf.cmd_prefix)
        async def settoken(msg: Message, *args):
            if utils.check_guild_token_exists(msg.ctx.guild.id):
                await reply(msg, '已配置token, 若需重设, 请先解绑')
            else:
                if len(args) != 1:
                    await reply(msg, '帮助: .settoken <token>')
                else:
                    utils.set_token(args[0], msg.ctx.guild.id)
                    await reply(msg, '绑定token成功')

        @self.bot.command(name='unsettoken', prefixes=self.conf.cmd_prefix)
        async def unsettoken(msg: Message, *args):
            if not utils.check_guild_token_exists(msg.ctx.guild.id):
                await reply(msg, '未配置token')
            else:
                perm = utils.get_perm(msg.ctx.guild.id)
                is_admin = perm.check_permission(msg.author_id, msg.author.roles, 'admin')
                if not is_admin:
                    await reply(msg, '您无权使用该指令')
                else:
                    utils.unset_token(msg.ctx.guild.id)
                    await reply(msg, '解绑token成功')

        @self.bot.command(name='setchannel', prefixes=self.conf.cmd_prefix)
        async def setchannel(msg: Message, *args):
            if not utils.check_guild_token_exists(msg.ctx.guild.id):
                await reply(msg, '未配置token')
            else:
                perm = utils.get_perm(msg.ctx.guild.id)
                is_admin = perm.check_permission(msg.author_id, msg.author.roles, 'admin')
                if not is_admin:
                    await reply(msg, '您无权使用该指令')
                else:
                    if len(args) == 0:
                        await reply(msg,
                                    '默认频道: .setchannel default\n恢复所有功能频道至默认: .setchannel reset\n设置单个功能频道: .setchannel <功能名称>')
                    elif len(args) == 1:
                        if args[0].lower() == 'default':
                            utils.set_channel(msg.ctx.guild.id, msg.ctx.channel.id, 'default')
                            await reply(msg, f'已设置默认频道为 {msg.ctx.channel.id}')
                        else:
                            if utils.check_function_status(args[0], guild=msg.ctx.guild.id):
                                utils.set_channel(msg.ctx.guild.id, msg.ctx.channel.id, args[0])
                                await reply(msg, f'已设置功能 {args[0]} 频道为 {msg.ctx.channel.id}')
                            else:
                                await reply(msg, '功能名称错误或功能未开启')
                    else:
                        await reply(msg,
                                    '参数错误\n默认频道: .setchannel default\n恢复所有功能频道至默认: .setchannel reset\n设置单个功能频道: .setchannel <功能名称>')

        @self.bot.command(name='info', prefixes=self.conf.cmd_prefix)
        async def info(msg: Message, *args):
            if not utils.check_guild_token_exists(msg.ctx.guild.id):
                await reply(msg, '未配置token')
            else:
                data = self.db.get(guild=msg.ctx.guild.id)
                message = ''
                for name, channel in data['channel'].items():
                    message += f'{name}: '
                    if channel == '-1':
                        message += '已禁用\n'
                    elif channel == '0':
                        if name == 'default':
                            message += '未设置\n'
                        else:
                            message += '已启用\n'
                    else:
                        if name == 'default':
                            message += f'{channel}\n'
                        else:
                            message += f'已启用 {channel}'
                message += f'tellraw: {data["tellraw"]}\n'
                await reply(msg, message)

        @self.bot.command(name='function', prefixes=self.conf.cmd_prefix)
        async def function(msg: Message, name='', boolean='', *args):
            if not utils.check_guild_token_exists(msg.ctx.guild.id):
                await reply(msg, '未配置token')
            else:
                perm = utils.get_perm(msg.ctx.guild.id)
                is_admin = perm.check_permission(msg.author_id, msg.author.roles, 'admin')
                if not is_admin:
                    await reply(msg, '您无权使用该指令')
                else:
                    if name == '' or boolean == '':
                        await reply(msg, '设置功能开关帮助: .function <功能名称> true/false')
                    else:
                        name = name.lower()
                        boolean = boolean.lower()
                        if not utils.check_function_exists(name):
                            await reply(msg, '该功能名称不存在')
                        else:
                            if boolean == 'true':
                                if utils.check_function_status(name, guild=msg.ctx.guild.id):
                                    await reply(msg, f'功能 {name} 处于开启状态, 无需开启')
                                else:
                                    if utils.set_channel(msg.ctx.guild.id, '0', name):
                                        await reply(msg, f'功能 {name} 已成功开启')
                                    else:
                                        await reply(msg, f'功能 {name} 开启失败, 请重试')
                            elif boolean == 'false':
                                if not utils.check_function_status(name, guild=msg.ctx.guild.id):
                                    await reply(msg, f'功能 {name} 处于关闭状态, 无需关闭')
                                else:
                                    if utils.set_channel(msg.ctx.guild.id, '-1', name):
                                        await reply(msg, f'功能 {name} 已成功关闭')
                                    else:
                                        await reply(msg, f'功能 {name} 关闭失败, 请重试')
                            else:
                                await reply(msg, '未知的布尔值')

        @self.bot.command(name='status', prefixes=self.conf.cmd_prefix)
        async def status(msg: Message, *args):
            if not utils.check_guild_token_exists(msg.ctx.guild.id):
                await reply(msg, '未配置token')
            else:
                perm = utils.get_perm(msg.ctx.guild.id)
                is_perm = perm.check_permission(msg.author_id, msg.author.roles, 'status') or perm.check_permission(
                    msg.author_id, msg.author.roles, 'admin')
                if not is_perm:
                    await reply(msg, '您无权使用该指令')
                else:
                    data = await get_all_status(utils.get_token(guild=msg.ctx.guild.id))
                    if data is None:
                        await reply(msg, '无服务器在线')
                    else:
                        message = ''
                        for server in data:
                            if data[server]['status'] == 'offline':
                                message += f'{server}: 离线\n'
                            else:
                                message += f'{server}: 在线\n    版本: {data[server]["version"]}\n    在线玩家: '
                                if len(data[server]['onlinePlayer']) == 0:
                                    message += '无\n'
                                else:
                                    online_players = ''
                                    for player in data[server]['onlinePlayer']:
                                        online_players += f'{player}, '
                                    online_players = online_players[:-2]
                                    message += f'{online_players}\n'
                        await reply(msg, message)

    async def get_role_list(self, guild_id: str) -> list:
        return dict(await self.bot.client.gate.requester.request(
            'GET', 'guild-role/list', params={'guild_id': guild_id}
        ))['items']

    async def send(self, channel: str, type: int, content: str):
        await self.bot.client.gate.requester.request('POST', 'message/create', json={
            'type': type,
            'target_id': channel,
            'content': content
        })

    def run(self):
        if self.bot is None:
            raise Exception('Must run after init')
        self.bot.run()
