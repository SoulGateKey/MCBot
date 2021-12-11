from bot import BotMain
from config import Config
from database import Database

conf = Config('config.json')
db = Database(conf.db)
bot = BotMain(conf, db)