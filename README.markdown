# 简介

ZPagedHScrollTable 是一个 Android 表格控件, 具备手势翻页功能, 并能支持对总宽度大于展现区域的数据列进行横向滚动, 实现移动设备上对大数据集的直观展示.

# 使用方法

1. 在界面 Layout XML 中添加 Custom & Library Views 组中的 ZPagedHScrollTable 控件
1. 实现 ZPagedHScrollTableDataAdapter 数据适配器接口，完成其中各个数据获取回调方法
1. 设置 ZPagedHScrollTable 控件的每页显示记录数和数据适配器对象，并调用其 refreshTable 方法完成内容显示. 后续翻页时, 控件会自行调用数据适配器完成数据加载, 不需要主控程序干预.

demo/ 目录下为一个实际的例子, 可供参考.

# 授权信息

本项目为 BSD 授权. 以下为正式的授权说明文本:

	Copyright (C) 2011 HandStar Co., Ltd. ( http://zhangxing.info ).
	All rights reserved.

	Redistribution and use in source and binary forms, with or without
	modification, are permitted provided that the following conditions
	are met:

		* Redistributions of source code must retain the above copyright
		notice, this list of conditions and the following disclaimer.

		* Redistributions in binary form must reproduce the above copyright
		notice, this list of conditions and the following disclaimer in the
		documentation and/or other materials provided with the distribution.

	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
	"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
	LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
	A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
	HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
	SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
	TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
	PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
	LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
	NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

