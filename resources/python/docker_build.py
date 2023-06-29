# coding:utf-8
from jinja2 import Environment, FileSystemLoader
import sys

class Obj(object):
    def __init__(self, param):
        self.param = param


def main():
    print('参数个数为:', len(sys.argv), '个参数。')
    print( '参数列表:', str(sys.argv))

    param = {
        "harborHost": "harbor.xyy.org.cn",
        "harborPort": "9888",
        "templateDir": sys.argv[1],
        "buildType": sys.argv[2],
        "jarPathFile": sys.argv[3],
        "deployServerName": sys.argv[4]
    }

    env = Environment(loader=FileSystemLoader(param["templateDir"]))
    if param["buildType"] == "web" :
        tpl = env.get_template('/docker_web_build_template.txt')
    else:
        tpl = env.get_template('/docker_springboot_build_template.txt')


    with open('Dockerfile', 'w+') as flout:
        render_content = tpl.render(obj=Obj(param))
        flout.write(render_content)


if __name__ == '__main__':
    main()
