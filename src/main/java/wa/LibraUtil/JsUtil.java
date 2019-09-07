package wa.LibraUtil;

public class JsUtil {

	static String image_alt() {
		StringBuilder jsc = new StringBuilder();
		jsc.append("var fname_flg = true;");
		jsc.append("var img = document.getElementsByTagName(\"img\");");
		jsc.append("for(var i=0; i<img.length; i++) {");
		jsc.append("var imgtag = img.item(i);");
		jsc.append("imgtag.setAttribute(\"style\", \"border:1px solid red;\");");
		jsc.append("var span_id = \"bkm-img-span-\" + i;");
		jsc.append("var src_val = imgtag.getAttribute(\"src\");");
		jsc.append("var fname = get_img_filename(src_val);");
		jsc.append("var alt_val = imgtag.getAttribute(\"alt\");");
		jsc.append("if(alt_val === null) {");
		jsc.append("alt_val = alt_attr_from_dirtycode(imgtag);");
		jsc.append("}");
		jsc.append("var html_str = \"\";");
		jsc.append("if(alt_attr_check(imgtag)) {");
		jsc.append("html_str += \"alt: \" + alt_val;");
		jsc.append("} else {");
		jsc.append("html_str += \"alt属性がない\";");
		jsc.append("}");
		jsc.append("if(fname_flg) {");
		jsc.append("if(html_str !== \"\") {");
		jsc.append("html_str += \", filename: \" + fname;");
		jsc.append("} else {");
		jsc.append("html_str += \"filename: \" + fname;");
		jsc.append("}");
		jsc.append("}");
		jsc.append("var css_txt = \"color:#fff;font-size:12px;padding:1px;background:#BF0000;\";");
		jsc.append("var span = '<span id=\"' + span_id + '\" style=\"' + css_txt + '\">' + html_str + '</span>';");
		jsc.append("imgtag.insertAdjacentHTML(\"beforebegin\", span);");
		jsc.append("}");
		jsc.append("tag_link_img();");
		jsc.append("function alt_attr_from_dirtycode(obj) {");
		jsc.append("var ret = \"\";");
		jsc.append("var imgtag = obj.outerHTML;");
		jsc.append("var pt = new RegExp('(alt=\")(.*?)(\")');");
		jsc.append("if(pt.test(imgtag)) {");
		jsc.append("ret = imgtag.match(pt)[2];");
		jsc.append("}");
		jsc.append("return ret;");
		jsc.append("}");
		jsc.append("function get_img_filename(str) {");
		jsc.append("var ret = \"\";");
		jsc.append("var pat = new RegExp(\"(.+)\\/(.+\\.)(JPG|jpg|GIF|gif|PNG|png|BMP|bmp)$\");");
		jsc.append("if(pat.test(str)) {");
		jsc.append("var arr = str.match(pat);");
		jsc.append("ret += arr[2] + arr[3];");
		jsc.append("}");
		jsc.append("return ret;");
		jsc.append("}");
		jsc.append("function alt_attr_check(imgtag) {");
		jsc.append("var txt = imgtag.outerHTML;");
		jsc.append("var pt1 = new RegExp('alt=\".*\"');");
		jsc.append("var pt2 = new RegExp('alt=');");
		jsc.append("if(pt1.test(txt) && pt2.test(txt)) return true;");
		jsc.append("else return false;");
		jsc.append("}");
		jsc.append("function tag_link_img() {");
		jsc.append("var ats = document.getElementsByTagName(\"a\");");
		jsc.append("var css_txt = \"border:2px dotted red;\";");
		jsc.append("for(var i=0; i<ats.length; i++) {");
		jsc.append("var atag = ats.item(i);");
		jsc.append("var imgs = atag.getElementsByTagName(\"img\");");
		jsc.append("for(var j=0; j<imgs.length; j++) {");
		jsc.append("var img = imgs.item(j);");
		jsc.append("img.setAttribute(\"style\", css_txt);");
		jsc.append("}");
		jsc.append("}");
		jsc.append("}");
		return jsc.toString();
	}

}
