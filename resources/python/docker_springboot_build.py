# coding:utf-8
import inspect
import os
import sys

from jinja2 import Environment, FileSystemLoader


class Obj(object):
    def __init__(self, param):
        self.param = param


def main():
    print('参数个数为:', len(sys.argv), '个参数。')
    print('参数列表:', str(sys.argv))

    current_file_name = inspect.getfile(inspect.currentframe())
    print(f"current_file_name: {current_file_name}")

    #获得d所在的目录,即d的父级目录
    parent_path = os.path.dirname(current_file_name)
    print(parent_path)

    env = Environment(loader=FileSystemLoader(parent_path))
    tpl = env.get_template('docker_springboot_build_template.txt')

    param = {
        "harborHost": sys.argv[1],
        "harborPort": sys.argv[2],
        "jarPathFile": sys.argv[3],
        "deployServerName": sys.argv[4]
    }

    with open('Dockerfile', 'w+') as flout:
        render_content = tpl.render(obj=Obj(param))
        flout.write(render_content)


if __name__ == '__main__':
    main()
