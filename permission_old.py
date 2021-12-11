class Permission:
    """权限类"""
    permission_data = None

    def __init__(self, data):
        self.permission_data = data

    def check_user_permission(self, user: str, permission: str) -> bool:
        if user in self.permission_data['user_permission']:
            if permission in self.permission_data['user_permission'][user]:
                return self.permission_data['user_permission'][user][permission]
            else:
                return False
        else:
            return False

    def check_group_permission(self, group: str, permission: str) -> bool:
        if group in self.permission_data['group_permission']:
            if permission in self.permission_data['group_permission'][group]:
                return self.permission_data['group_permission'][group][permission]
            else:
                return False
        else:
            return False

    def get_groups_in_role(self, role: str) -> list:
        if str(role) in self.permission_data['role']:
            return self.permission_data['role']
        else:
            return []

    def check_permission(self, user: str, role: list, permission: str) -> bool:
        if user in self.permission_data['user_permission']:
            if permission in self.permission_data['user_permission'][user]:
                return self.permission_data['user_permission'][user][permission]
            else:
                for k, v in self.permission_data['user_permission'][user].items():
                    if k.startswith('group.') and v is True:
                        if self.check_group_permission(k.replace('group.', ''), permission):
                            return True
                for n in role:
                    for i in self.get_groups_in_role(str(n)):
                        if i in self.permission_data['group_permission']:
                            if self.check_group_permission(i, permission):
                                return True
                return False
        else:
            return False

    def del_user_permission(self, user: str, permission: str) -> bool:
        if user in self.permission_data['user_permission']:
            if permission in self.permission_data['user_permission'][user]:
                del self.permission_data['user_permission'][user][permission]
                return True
            else:
                return False
        else:
            return False

    def del_group_permission(self, group: str, permission: str) -> bool:
        if group in self.permission_data['group_permission']:
            if permission in self.permission_data['group_permission'][group]:
                del self.permission_data['group_permission'][group][permission]
                return True
            else:
                return False
        else:
            return False

    def del_group(self, group: str) -> bool:
        if group in self.permission_data['group_permission']:
            del self.permission_data['group_permission'][group]
            return True
        else:
            return False

    def del_user(self, user: str) -> bool:
        if user in self.permission_data['user_permission']:
            del self.permission_data['user_permission'][user]
            return True
        else:
            return False

    def add_user_permission(self, user: str, permission: str, boolean: bool = True) -> bool:
        if user not in self.permission_data['user_permission']:
            self.permission_data['user_permission'][user] = {}
        if permission not in self.permission_data['user_permission'][user]:
            self.permission_data['user_permission'][user][permission] = boolean
            return True
        else:
            return False

    def add_group_permission(self, group: str, permission: str, boolean: bool = True) -> bool:
        if group not in self.permission_data['group_permission']:
            self.permission_data['group_permission'][group] = {}
        if permission not in self.permission_data['group_permission'][group]:
            self.permission_data['group_permission'][group][permission] = boolean
            return True
        else:
            return False

    def get_dict(self) -> dict:
        return self.permission_data

    def add_group_to_role(self, group: str, role: str) -> bool:
        if group in self.permission_data['group_permission'][group]:
            if role not in self.permission_data['role']:
                self.permission_data['role'][role] = []
            self.permission_data['role'][role].append(str(role))
            return True
        else:
            return False

    def del_group_in_role(self, group: str, role: str) -> bool:
        if role in self.permission_data['role'] and group in self.permission_data['role'][role]:
            self.permission_data['role'][role].remove(str(role))
            return True
        else:
            return False
