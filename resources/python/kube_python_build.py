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

    # 获得d所在的目录,即d的父级目录
    parent_path = os.path.dirname(current_file_name)
    print(parent_path)

    env = Environment(loader=FileSystemLoader(parent_path))
    tpl = env.get_template('kube_python_build_template.yaml')

    param = {
        "metadataNameSpace": "wict",
        "metadataName": sys.argv[1],
        "containersImage": sys.argv[2],
        "containerPort": sys.argv[3],
        "env": sys.argv[4],
        "ci": sys.argv[5],
        "project": sys.argv[6],
        "javaOpts": sys.argv[7]
    }

    with open('k8s.yaml', 'w+') as flout:
        render_content = tpl.render(obj=Obj(param))
        flout.write(render_content)


if __name__ == '__main__':
    main()
