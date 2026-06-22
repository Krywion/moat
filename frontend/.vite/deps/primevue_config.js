import { Ft as onMounted, Gn as readonly, Kn as ref, Ot as nextTick, Wn as reactive, gn as watch, ut as getCurrentInstance, xt as inject } from "./vue.runtime.esm-bundler-BbnC8aGa.js";
//#region node_modules/@primeuix/utils/dist/object/index.mjs
var ie$1 = Object.defineProperty;
var K = Object.getOwnPropertySymbols;
var se = Object.prototype.hasOwnProperty, ae$1 = Object.prototype.propertyIsEnumerable;
var N$1 = (e, t, n) => t in e ? ie$1(e, t, {
	enumerable: !0,
	configurable: !0,
	writable: !0,
	value: n
}) : e[t] = n, d = (e, t) => {
	for (var n in t || (t = {})) se.call(t, n) && N$1(e, n, t[n]);
	if (K) for (var n of K(t)) ae$1.call(t, n) && N$1(e, n, t[n]);
	return e;
};
function l(e) {
	return e == null || e === "" || Array.isArray(e) && e.length === 0 || !(e instanceof Date) && typeof e == "object" && Object.keys(e).length === 0;
}
function c$1(e) {
	return typeof e == "function" && "call" in e && "apply" in e;
}
function s$1(e) {
	return !l(e);
}
function i(e, t = !0) {
	return e instanceof Object && e.constructor === Object && (t || Object.keys(e).length !== 0);
}
function $$1(e = {}, t = {}) {
	let n = d({}, e);
	return Object.keys(t).forEach((o) => {
		let r = o;
		i(t[r]) && r in e && i(e[r]) ? n[r] = $$1(e[r], t[r]) : n[r] = t[r];
	}), n;
}
function w(...e) {
	return e.reduce((t, n, o) => o === 0 ? n : $$1(t, n), {});
}
function m(e, ...t) {
	return c$1(e) ? e(...t) : e;
}
function a(e, t = !0) {
	return typeof e == "string" && (t || e !== "");
}
function z(e) {
	return s$1(e) && !isNaN(e);
}
function G(e, t) {
	if (t) {
		let n = t.test(e);
		return t.lastIndex = 0, n;
	}
	return !1;
}
function H(...e) {
	return w(...e);
}
function Y$1(e) {
	return e && e.replace(/\/\*(?:(?!\*\/)[\s\S])*\*\/|[\r\n\t]+/g, "").replace(/ {2,}/g, " ").replace(/ ([{:}]) /g, "$1").replace(/([;,]) /g, "$1").replace(/ !/g, "!").replace(/: /g, ":").trim();
}
function re(e) {
	return a(e) ? e.replace(/(_)/g, "-").replace(/([a-z])([A-Z])/g, "$1-$2").toLowerCase() : e;
}
//#endregion
//#region node_modules/@primeuix/utils/dist/eventbus/index.mjs
function s() {
	let r = /* @__PURE__ */ new Map();
	return {
		on(e, t) {
			let n = r.get(e);
			return n ? n.push(t) : n = [t], r.set(e, n), this;
		},
		off(e, t) {
			let n = r.get(e);
			return n && n.splice(n.indexOf(t) >>> 0, 1), this;
		},
		emit(e, t) {
			let n = r.get(e);
			n && n.forEach((i) => {
				i(t);
			});
		},
		clear() {
			r.clear();
		}
	};
}
//#endregion
//#region node_modules/@primeuix/utils/dist/dom/index.mjs
function y(t) {
	if (t) {
		let e = t.parentNode;
		return e && e instanceof ShadowRoot && e.host && (e = e.host), e;
	}
	return null;
}
function T(t) {
	return !!(t !== null && typeof t != "undefined" && t.nodeName && y(t));
}
function c(t) {
	return typeof Element != "undefined" ? t instanceof Element : t !== null && typeof t == "object" && t.nodeType === 1 && typeof t.nodeName == "string";
}
function A(t, e = {}) {
	if (c(t)) {
		let o = (n, r) => {
			var l, d;
			let i = (l = t == null ? void 0 : t.$attrs) != null && l[n] ? [(d = t == null ? void 0 : t.$attrs) == null ? void 0 : d[n]] : [];
			return [r].flat().reduce((s, a) => {
				if (a != null) {
					let u = typeof a;
					if (u === "string" || u === "number") s.push(a);
					else if (u === "object") {
						let p = Array.isArray(a) ? o(n, a) : Object.entries(a).map(([f, g]) => n === "style" && (g || g === 0) ? `${f.replace(/([a-z])([A-Z])/g, "$1-$2").toLowerCase()}:${g}` : g ? f : void 0);
						s = p.length ? s.concat(p.filter((f) => !!f)) : s;
					}
				}
				return s;
			}, i);
		};
		Object.entries(e).forEach(([n, r]) => {
			if (r != null) {
				let i = n.match(/^on(.+)/);
				i ? t.addEventListener(i[1].toLowerCase(), r) : n === "p-bind" || n === "pBind" ? A(t, r) : (r = n === "class" ? [...new Set(o("class", r))].join(" ").trim() : n === "style" ? o("style", r).join(";").trim() : r, (t.$attrs = t.$attrs || {}) && (t.$attrs[n] = r), t.setAttribute(n, r));
			}
		});
	}
}
function tt() {
	return !!(typeof window != "undefined" && window.document && window.document.createElement);
}
function _t(t, e = "", o) {
	c(t) && o !== null && o !== void 0 && t.setAttribute(e, o);
}
//#endregion
//#region node_modules/@primeuix/styled/dist/index.mjs
var rt = Object.defineProperty, st = Object.defineProperties;
var nt = Object.getOwnPropertyDescriptors;
var F = Object.getOwnPropertySymbols;
var xe = Object.prototype.hasOwnProperty, be = Object.prototype.propertyIsEnumerable;
var _e = (e, t, r) => t in e ? rt(e, t, {
	enumerable: !0,
	configurable: !0,
	writable: !0,
	value: r
}) : e[t] = r, h = (e, t) => {
	for (var r in t || (t = {})) xe.call(t, r) && _e(e, r, t[r]);
	if (F) for (var r of F(t)) be.call(t, r) && _e(e, r, t[r]);
	return e;
}, $ = (e, t) => st(e, nt(t));
var v = (e, t) => {
	var r = {};
	for (var s in e) xe.call(e, s) && t.indexOf(s) < 0 && (r[s] = e[s]);
	if (e != null && F) for (var s of F(e)) t.indexOf(s) < 0 && be.call(e, s) && (r[s] = e[s]);
	return r;
};
var N = s();
var k = /{([^}]*)}/g, ne = /(\d+\s+[\+\-\*\/]\s+\d+)/g, ie = /var\([^)]+\)/g;
function oe(e) {
	return a(e) ? e.replace(/[A-Z]/g, (t, r) => r === 0 ? t : "." + t.toLowerCase()).toLowerCase() : e;
}
function ve(e) {
	return i(e) && e.hasOwnProperty("$value") && e.hasOwnProperty("$type") ? e.$value : e;
}
function dt(e) {
	return e.replaceAll(/ /g, "").replace(/[^\w]/g, "-");
}
function Q(e = "", t = "") {
	return dt(`${a(e, !1) && a(t, !1) ? `${e}-` : e}${t}`);
}
function ae(e = "", t = "") {
	return `--${Q(e, t)}`;
}
function ht(e = "") {
	return ((e.match(/{/g) || []).length + (e.match(/}/g) || []).length) % 2 !== 0;
}
function Y(e, t = "", r = "", s = [], i) {
	if (a(e)) {
		let a = e.trim();
		if (ht(a)) return;
		if (G(a, k)) {
			let n = a.replaceAll(k, (l) => {
				return `var(${ae(r, re(l.replace(/{|}/g, "").split(".").filter((m) => !s.some((d) => G(m, d))).join("-")))}${s$1(i) ? `, ${i}` : ""})`;
			});
			return G(n.replace(ie, "0"), ne) ? `calc(${n})` : n;
		}
		return a;
	} else if (z(e)) return e;
}
function Re(e, t, r) {
	a(t, !1) && e.push(`${t}:${r};`);
}
function C(e, t) {
	return e ? `${e}{${t}}` : "";
}
function le(e, t) {
	if (e.indexOf("dt(") === -1) return e;
	function r(n, l) {
		let o = [], c = 0, m = "", d = null, u = 0;
		for (; c <= n.length;) {
			let g = n[c];
			if ((g === "\"" || g === "'" || g === "`") && n[c - 1] !== "\\" && (d = d === g ? null : g), !d && (g === "(" && u++, g === ")" && u--, (g === "," || c === n.length) && u === 0)) {
				let f = m.trim();
				f.startsWith("dt(") ? o.push(le(f, l)) : o.push(s(f)), m = "", c++;
				continue;
			}
			g !== void 0 && (m += g), c++;
		}
		return o;
	}
	function s(n) {
		let l = n[0];
		if ((l === "\"" || l === "'" || l === "`") && n[n.length - 1] === l) return n.slice(1, -1);
		let o = Number(n);
		return isNaN(o) ? n : o;
	}
	let i = [], a = [];
	for (let n = 0; n < e.length; n++) if (e[n] === "d" && e.slice(n, n + 3) === "dt(") a.push(n), n += 2;
	else if (e[n] === ")" && a.length > 0) {
		let l = a.pop();
		a.length === 0 && i.push([l, n]);
	}
	if (!i.length) return e;
	for (let n = i.length - 1; n >= 0; n--) {
		let [l, o] = i[n], d = t(...r(e.slice(l + 3, o), t));
		e = e.slice(0, l) + d + e.slice(o + 1);
	}
	return e;
}
var E = (...e) => ue(S.getTheme(), ...e), ue = (e = {}, t, r, s) => {
	if (t) {
		let { variable: i, options: a } = S.defaults || {}, { prefix: n, transform: l$2 } = (e == null ? void 0 : e.options) || a || {}, o = G(t, k) ? t : `{${t}}`;
		return s === "value" || l(s) && l$2 === "strict" ? S.getTokenValue(t) : Y(o, void 0, n, [i.excludedKeyRegex], r);
	}
	return "";
};
function ar(e, ...t) {
	if (e instanceof Array) return le(e.reduce((s, i, a) => {
		var n;
		return s + i + ((n = m(t[a], { dt: E })) != null ? n : "");
	}, ""), E);
	return m(e, { dt: E });
}
function de(e, t = {}) {
	let r = S.defaults.variable, { prefix: s = r.prefix, selector: i$4 = r.selector, excludedKeyRegex: a = r.excludedKeyRegex } = t, n = [], l = [], o = [{
		node: e,
		path: s
	}];
	for (; o.length;) {
		let { node: m, path: d } = o.pop();
		for (let u in m) {
			let g = m[u], f = ve(g), p = G(u, a) ? Q(d) : Q(d, re(u));
			if (i(f)) o.push({
				node: f,
				path: p
			});
			else {
				Re(l, ae(p), Y(f, p, s, [a]));
				let T = p;
				s && T.startsWith(s + "-") && (T = T.slice(s.length + 1)), n.push(T.replace(/-/g, "."));
			}
		}
	}
	let c = l.join("");
	return {
		value: l,
		tokens: n,
		declarations: c,
		css: C(i$4, c)
	};
}
var b = {
	regex: {
		rules: {
			class: {
				pattern: /^\.([a-zA-Z][\w-]*)$/,
				resolve(e) {
					return {
						type: "class",
						selector: e,
						matched: this.pattern.test(e.trim())
					};
				}
			},
			attr: {
				pattern: /^\[(.*)\]$/,
				resolve(e) {
					return {
						type: "attr",
						selector: `:root${e},:host${e}`,
						matched: this.pattern.test(e.trim())
					};
				}
			},
			media: {
				pattern: /^@media (.*)$/,
				resolve(e) {
					return {
						type: "media",
						selector: e,
						matched: this.pattern.test(e.trim())
					};
				}
			},
			system: {
				pattern: /^system$/,
				resolve(e) {
					return {
						type: "system",
						selector: "@media (prefers-color-scheme: dark)",
						matched: this.pattern.test(e.trim())
					};
				}
			},
			custom: { resolve(e) {
				return {
					type: "custom",
					selector: e,
					matched: !0
				};
			} }
		},
		resolve(e) {
			let t = Object.keys(this.rules).filter((r) => r !== "custom").map((r) => this.rules[r]);
			return [e].flat().map((r) => {
				var s;
				return (s = t.map((i) => i.resolve(r)).find((i) => i.matched)) != null ? s : this.rules.custom.resolve(r);
			});
		}
	},
	_toVariables(e, t) {
		return de(e, { prefix: t == null ? void 0 : t.prefix });
	},
	getCommon({ name: e = "", theme: t = {}, params: r, set: s, defaults: i }) {
		var R, T, j, O, M, z, V;
		let { preset: a, options: n } = t, l, o, c, m$1, d, u, g;
		if (s$1(a) && n.transform !== "strict") {
			let { primitive: L, semantic: te, extend: re } = a, f = te || {}, { colorScheme: K } = f, A = v(f, ["colorScheme"]), x = re || {}, { colorScheme: X } = x, G = v(x, ["colorScheme"]), p = K || {}, { dark: U } = p, B = v(p, ["dark"]), y = X || {}, { dark: I } = y, H = v(y, ["dark"]), W = s$1(L) ? this._toVariables({ primitive: L }, n) : {}, q = s$1(A) ? this._toVariables({ semantic: A }, n) : {}, Z = s$1(B) ? this._toVariables({ light: B }, n) : {}, pe = s$1(U) ? this._toVariables({ dark: U }, n) : {}, fe = s$1(G) ? this._toVariables({ semantic: G }, n) : {}, ye = s$1(H) ? this._toVariables({ light: H }, n) : {}, Se = s$1(I) ? this._toVariables({ dark: I }, n) : {}, [Me, ze] = [(R = W.declarations) != null ? R : "", W.tokens], [Ke, Xe] = [(T = q.declarations) != null ? T : "", q.tokens || []], [Ge, Ue] = [(j = Z.declarations) != null ? j : "", Z.tokens || []], [Be, Ie] = [(O = pe.declarations) != null ? O : "", pe.tokens || []], [He, We] = [(M = fe.declarations) != null ? M : "", fe.tokens || []], [qe, Ze] = [(z = ye.declarations) != null ? z : "", ye.tokens || []], [Fe, Je] = [(V = Se.declarations) != null ? V : "", Se.tokens || []];
			l = this.transformCSS(e, Me, "light", "variable", n, s, i), o = ze;
			c = `${this.transformCSS(e, `${Ke}${Ge}`, "light", "variable", n, s, i)}${this.transformCSS(e, `${Be}`, "dark", "variable", n, s, i)}`, m$1 = [...new Set([
				...Xe,
				...Ue,
				...Ie
			])];
			d = `${this.transformCSS(e, `${He}${qe}color-scheme:light`, "light", "variable", n, s, i)}${this.transformCSS(e, `${Fe}color-scheme:dark`, "dark", "variable", n, s, i)}`, u = [...new Set([
				...We,
				...Ze,
				...Je
			])], g = m(a.css, { dt: E });
		}
		return {
			primitive: {
				css: l,
				tokens: o
			},
			semantic: {
				css: c,
				tokens: m$1
			},
			global: {
				css: d,
				tokens: u
			},
			style: g
		};
	},
	getPreset({ name: e = "", preset: t = {}, options: r, params: s, set: i, defaults: a, selector: n }) {
		var f, x, p;
		let l, o, c;
		if (s$1(t) && r.transform !== "strict") {
			let y = e.replace("-directive", ""), m$2 = t, { colorScheme: R, extend: T, css: j } = m$2, O = v(m$2, [
				"colorScheme",
				"extend",
				"css"
			]), d = T || {}, { colorScheme: M } = d, z = v(d, ["colorScheme"]), u = R || {}, { dark: V } = u, L = v(u, ["dark"]), g = M || {}, { dark: te } = g, re = v(g, ["dark"]), K = s$1(O) ? this._toVariables({ [y]: h(h({}, O), z) }, r) : {}, A = s$1(L) ? this._toVariables({ [y]: h(h({}, L), re) }, r) : {}, X = s$1(V) ? this._toVariables({ [y]: h(h({}, V), te) }, r) : {}, [G, U] = [(f = K.declarations) != null ? f : "", K.tokens || []], [B, I] = [(x = A.declarations) != null ? x : "", A.tokens || []], [H, W] = [(p = X.declarations) != null ? p : "", X.tokens || []];
			l = `${this.transformCSS(y, `${G}${B}`, "light", "variable", r, i, a, n)}${this.transformCSS(y, H, "dark", "variable", r, i, a, n)}`, o = [...new Set([
				...U,
				...I,
				...W
			])], c = m(j, { dt: E });
		}
		return {
			css: l,
			tokens: o,
			style: c
		};
	},
	getPresetC({ name: e = "", theme: t = {}, params: r, set: s, defaults: i }) {
		var o;
		let { preset: a, options: n } = t, l = (o = a == null ? void 0 : a.components) == null ? void 0 : o[e];
		return this.getPreset({
			name: e,
			preset: l,
			options: n,
			params: r,
			set: s,
			defaults: i
		});
	},
	getPresetD({ name: e = "", theme: t = {}, params: r, set: s, defaults: i }) {
		var c, m;
		let a = e.replace("-directive", ""), { preset: n, options: l } = t, o = ((c = n == null ? void 0 : n.components) == null ? void 0 : c[a]) || ((m = n == null ? void 0 : n.directives) == null ? void 0 : m[a]);
		return this.getPreset({
			name: a,
			preset: o,
			options: l,
			params: r,
			set: s,
			defaults: i
		});
	},
	applyDarkColorScheme(e) {
		return !(e.darkModeSelector === "none" || e.darkModeSelector === !1);
	},
	getColorSchemeOption(e, t) {
		var r;
		return this.applyDarkColorScheme(e) ? this.regex.resolve(e.darkModeSelector === !0 ? t.options.darkModeSelector : (r = e.darkModeSelector) != null ? r : t.options.darkModeSelector) : [];
	},
	getLayerOrder(e, t = {}, r, s) {
		let { cssLayer: i } = t;
		return i ? `@layer ${m(i.order || i.name || "primeui", r)}` : "";
	},
	getCommonStyleSheet({ name: e = "", theme: t = {}, params: r, props: s = {}, set: i$1, defaults: a }) {
		let n = this.getCommon({
			name: e,
			theme: t,
			params: r,
			set: i$1,
			defaults: a
		}), l = Object.entries(s).reduce((o, [c, m]) => o.push(`${c}="${m}"`) && o, []).join(" ");
		return Object.entries(n || {}).reduce((o, [c, m]) => {
			if (i(m) && Object.hasOwn(m, "css")) {
				let d = Y$1(m.css), u = `${c}-variables`;
				o.push(`<style type="text/css" data-primevue-style-id="${u}" ${l}>${d}</style>`);
			}
			return o;
		}, []).join("");
	},
	getStyleSheet({ name: e = "", theme: t = {}, params: r, props: s = {}, set: i, defaults: a }) {
		var c;
		let n = {
			name: e,
			theme: t,
			params: r,
			set: i,
			defaults: a
		}, l = (c = e.includes("-directive") ? this.getPresetD(n) : this.getPresetC(n)) == null ? void 0 : c.css, o = Object.entries(s).reduce((m, [d, u]) => m.push(`${d}="${u}"`) && m, []).join(" ");
		return l ? `<style type="text/css" data-primevue-style-id="${e}-variables" ${o}>${Y$1(l)}</style>` : "";
	},
	createTokens(e = {}, t, r = "", s = "", i$2 = {}) {
		let a = function(l$1, o = {}, c = []) {
			if (c.includes(this.path)) return console.warn(`Circular reference detected at ${this.path}`), {
				colorScheme: l$1,
				path: this.path,
				paths: o,
				value: void 0
			};
			c.push(this.path), o.name = this.path, o.binding || (o.binding = {});
			let m = this.value;
			if (typeof this.value == "string" && k.test(this.value)) {
				let u = this.value.trim().replace(k, (g) => {
					var y;
					let f = g.slice(1, -1), x = this.tokens[f];
					if (!x) return console.warn(`Token not found for path: ${f}`), "__UNRESOLVED__";
					let p = x.computed(l$1, o, c);
					return Array.isArray(p) && p.length === 2 ? `light-dark(${p[0].value},${p[1].value})` : (y = p == null ? void 0 : p.value) != null ? y : "__UNRESOLVED__";
				});
				m = ne.test(u.replace(ie, "0")) ? `calc(${u})` : u;
			}
			return l(o.binding) && delete o.binding, c.pop(), {
				colorScheme: l$1,
				path: this.path,
				paths: o,
				value: m.includes("__UNRESOLVED__") ? void 0 : m
			};
		}, n = (l, o, c) => {
			Object.entries(l).forEach(([m, d]) => {
				let u = G(m, t.variable.excludedKeyRegex) ? o : o ? `${o}.${oe(m)}` : oe(m), g = c ? `${c}.${m}` : m;
				i(d) ? n(d, u, g) : (i$2[u] || (i$2[u] = {
					paths: [],
					computed: (f, x = {}, p = []) => {
						if (i$2[u].paths.length === 1) return i$2[u].paths[0].computed(i$2[u].paths[0].scheme, x.binding, p);
						if (f && f !== "none") for (let y = 0; y < i$2[u].paths.length; y++) {
							let R = i$2[u].paths[y];
							if (R.scheme === f) return R.computed(f, x.binding, p);
						}
						return i$2[u].paths.map((y) => y.computed(y.scheme, x[y.scheme], p));
					}
				}), i$2[u].paths.push({
					path: g,
					value: d,
					scheme: g.includes("colorScheme.light") ? "light" : g.includes("colorScheme.dark") ? "dark" : "none",
					computed: a,
					tokens: i$2
				}));
			});
		};
		return n(e, r, s), i$2;
	},
	getTokenValue(e, t, r) {
		var l;
		let i = ((o) => o.split(".").filter((m) => !G(m.toLowerCase(), r.variable.excludedKeyRegex)).join("."))(t), a = t.includes("colorScheme.light") ? "light" : t.includes("colorScheme.dark") ? "dark" : void 0, n = [(l = e[i]) == null ? void 0 : l.computed(a)].flat().filter((o) => o);
		return n.length === 1 ? n[0].value : n.reduce((o = {}, c) => {
			let u = c, { colorScheme: m } = u;
			return o[m] = v(u, ["colorScheme"]), o;
		}, void 0);
	},
	getSelectorRule(e, t, r, s) {
		return r === "class" || r === "attr" ? C(s$1(t) ? `${e}${t},${e} ${t}` : e, s) : C(e, C(t != null ? t : ":root,:host", s));
	},
	transformCSS(e, t, r, s, i$3 = {}, a, n, l) {
		if (s$1(t)) {
			let { cssLayer: o } = i$3;
			if (s !== "style") {
				let c = this.getColorSchemeOption(i$3, n);
				t = r === "dark" ? c.reduce((m, { type: d, selector: u }) => (s$1(u) && (m += u.includes("[CSS]") ? u.replace("[CSS]", t) : this.getSelectorRule(u, l, d, t)), m), "") : C(l != null ? l : ":root,:host", t);
			}
			if (o) {
				let c = {
					name: "primeui",
					order: "primeui"
				};
				i(o) && (c.name = m(o.name, {
					name: e,
					type: s
				})), s$1(c.name) && (t = C(`@layer ${c.name}`, t), a?.layerNames(c.name));
			}
			return t;
		}
		return "";
	}
};
var S = {
	defaults: {
		variable: {
			prefix: "p",
			selector: ":root,:host",
			excludedKeyRegex: /^(primitive|semantic|components|directives|variables|colorscheme|light|dark|common|root|states|extend|css)$/gi
		},
		options: {
			prefix: "p",
			darkModeSelector: "system",
			cssLayer: !1
		}
	},
	_theme: void 0,
	_layerNames: /* @__PURE__ */ new Set(),
	_loadedStyleNames: /* @__PURE__ */ new Set(),
	_loadingStyles: /* @__PURE__ */ new Set(),
	_tokens: {},
	update(e = {}) {
		let { theme: t } = e;
		t && (this._theme = $(h({}, t), { options: h(h({}, this.defaults.options), t.options) }), this._tokens = b.createTokens(this.preset, this.defaults), this.clearLoadedStyleNames());
	},
	get theme() {
		return this._theme;
	},
	get preset() {
		var e;
		return ((e = this.theme) == null ? void 0 : e.preset) || {};
	},
	get options() {
		var e;
		return ((e = this.theme) == null ? void 0 : e.options) || {};
	},
	get tokens() {
		return this._tokens;
	},
	getTheme() {
		return this.theme;
	},
	setTheme(e) {
		this.update({ theme: e }), N.emit("theme:change", e);
	},
	getPreset() {
		return this.preset;
	},
	setPreset(e) {
		this._theme = $(h({}, this.theme), { preset: e }), this._tokens = b.createTokens(e, this.defaults), this.clearLoadedStyleNames(), N.emit("preset:change", e), N.emit("theme:change", this.theme);
	},
	getOptions() {
		return this.options;
	},
	setOptions(e) {
		this._theme = $(h({}, this.theme), { options: e }), this.clearLoadedStyleNames(), N.emit("options:change", e), N.emit("theme:change", this.theme);
	},
	getLayerNames() {
		return [...this._layerNames];
	},
	setLayerNames(e) {
		this._layerNames.add(e);
	},
	getLoadedStyleNames() {
		return this._loadedStyleNames;
	},
	isStyleNameLoaded(e) {
		return this._loadedStyleNames.has(e);
	},
	setLoadedStyleName(e) {
		this._loadedStyleNames.add(e);
	},
	deleteLoadedStyleName(e) {
		this._loadedStyleNames.delete(e);
	},
	clearLoadedStyleNames() {
		this._loadedStyleNames.clear();
	},
	getTokenValue(e) {
		return b.getTokenValue(this.tokens, e, this.defaults);
	},
	getCommon(e = "", t) {
		return b.getCommon({
			name: e,
			theme: this.theme,
			params: t,
			defaults: this.defaults,
			set: { layerNames: this.setLayerNames.bind(this) }
		});
	},
	getComponent(e = "", t) {
		let r = {
			name: e,
			theme: this.theme,
			params: t,
			defaults: this.defaults,
			set: { layerNames: this.setLayerNames.bind(this) }
		};
		return b.getPresetC(r);
	},
	getDirective(e = "", t) {
		let r = {
			name: e,
			theme: this.theme,
			params: t,
			defaults: this.defaults,
			set: { layerNames: this.setLayerNames.bind(this) }
		};
		return b.getPresetD(r);
	},
	getCustomPreset(e = "", t, r, s) {
		let i = {
			name: e,
			preset: t,
			options: this.options,
			selector: r,
			params: s,
			defaults: this.defaults,
			set: { layerNames: this.setLayerNames.bind(this) }
		};
		return b.getPreset(i);
	},
	getLayerOrderCSS(e = "") {
		return b.getLayerOrder(e, this.options, { names: this.getLayerNames() }, this.defaults);
	},
	transformCSS(e = "", t, r = "style", s) {
		return b.transformCSS(e, t, s, r, this.options, { layerNames: this.setLayerNames.bind(this) }, this.defaults);
	},
	getCommonStyleSheet(e = "", t, r = {}) {
		return b.getCommonStyleSheet({
			name: e,
			theme: this.theme,
			params: t,
			props: r,
			defaults: this.defaults,
			set: { layerNames: this.setLayerNames.bind(this) }
		});
	},
	getStyleSheet(e, t, r = {}) {
		return b.getStyleSheet({
			name: e,
			theme: this.theme,
			params: t,
			props: r,
			defaults: this.defaults,
			set: { layerNames: this.setLayerNames.bind(this) }
		});
	},
	onStyleMounted(e) {
		this._loadingStyles.add(e);
	},
	onStyleUpdated(e) {
		this._loadingStyles.add(e);
	},
	onStyleLoaded(e, { name: t }) {
		this._loadingStyles.size && (this._loadingStyles.delete(t), N.emit(`theme:${t}:load`, e), !this._loadingStyles.size && N.emit("theme:load"));
	}
};
//#endregion
//#region node_modules/@primevue/core/api/index.mjs
var FilterMatchMode = {
	STARTS_WITH: "startsWith",
	CONTAINS: "contains",
	NOT_CONTAINS: "notContains",
	ENDS_WITH: "endsWith",
	EQUALS: "equals",
	NOT_EQUALS: "notEquals",
	IN: "in",
	LESS_THAN: "lt",
	LESS_THAN_OR_EQUAL_TO: "lte",
	GREATER_THAN: "gt",
	GREATER_THAN_OR_EQUAL_TO: "gte",
	BETWEEN: "between",
	DATE_IS: "dateIs",
	DATE_IS_NOT: "dateIsNot",
	DATE_BEFORE: "dateBefore",
	DATE_AFTER: "dateAfter"
};
//#endregion
//#region node_modules/@primeuix/styles/dist/base/index.mjs
var style = "\n    *,\n    ::before,\n    ::after {\n        box-sizing: border-box;\n    }\n\n    .p-collapsible-enter-active {\n        animation: p-animate-collapsible-expand 0.2s ease-out;\n        overflow: hidden;\n    }\n\n    .p-collapsible-leave-active {\n        animation: p-animate-collapsible-collapse 0.2s ease-out;\n        overflow: hidden;\n    }\n\n    @keyframes p-animate-collapsible-expand {\n        from {\n            grid-template-rows: 0fr;\n        }\n        to {\n            grid-template-rows: 1fr;\n        }\n    }\n\n    @keyframes p-animate-collapsible-collapse {\n        from {\n            grid-template-rows: 1fr;\n        }\n        to {\n            grid-template-rows: 0fr;\n        }\n    }\n\n    .p-disabled,\n    .p-disabled * {\n        cursor: default;\n        pointer-events: none;\n        user-select: none;\n    }\n\n    .p-disabled,\n    .p-component:disabled {\n        opacity: dt('disabled.opacity');\n    }\n\n    .pi {\n        font-size: dt('icon.size');\n    }\n\n    .p-icon {\n        width: dt('icon.size');\n        height: dt('icon.size');\n    }\n\n    .p-overlay-mask {\n        background: var(--px-mask-background, dt('mask.background'));\n        color: dt('mask.color');\n        position: fixed;\n        top: 0;\n        left: 0;\n        width: 100%;\n        height: 100%;\n    }\n\n    .p-overlay-mask-enter-active {\n        animation: p-animate-overlay-mask-enter dt('mask.transition.duration') forwards;\n    }\n\n    .p-overlay-mask-leave-active {\n        animation: p-animate-overlay-mask-leave dt('mask.transition.duration') forwards;\n    }\n\n    @keyframes p-animate-overlay-mask-enter {\n        from {\n            background: transparent;\n        }\n        to {\n            background: var(--px-mask-background, dt('mask.background'));\n        }\n    }\n    @keyframes p-animate-overlay-mask-leave {\n        from {\n            background: var(--px-mask-background, dt('mask.background'));\n        }\n        to {\n            background: transparent;\n        }\n    }\n\n    .p-anchored-overlay-enter-active {\n        animation: p-animate-anchored-overlay-enter 300ms cubic-bezier(.19,1,.22,1);\n    }\n\n    .p-anchored-overlay-leave-active {\n        animation: p-animate-anchored-overlay-leave 300ms cubic-bezier(.19,1,.22,1);\n    }\n\n    @keyframes p-animate-anchored-overlay-enter {\n        from {\n            opacity: 0;\n            transform: scale(0.93);\n        }\n    }\n\n    @keyframes p-animate-anchored-overlay-leave {\n        to {\n            opacity: 0;\n            transform: scale(0.93);\n        }\n    }\n";
//#endregion
//#region node_modules/@primevue/core/usestyle/index.mjs
function _typeof$2(o) {
	"@babel/helpers - typeof";
	return _typeof$2 = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ? function(o) {
		return typeof o;
	} : function(o) {
		return o && "function" == typeof Symbol && o.constructor === Symbol && o !== Symbol.prototype ? "symbol" : typeof o;
	}, _typeof$2(o);
}
function ownKeys$2(e, r) {
	var t = Object.keys(e);
	if (Object.getOwnPropertySymbols) {
		var o = Object.getOwnPropertySymbols(e);
		r && (o = o.filter(function(r) {
			return Object.getOwnPropertyDescriptor(e, r).enumerable;
		})), t.push.apply(t, o);
	}
	return t;
}
function _objectSpread$2(e) {
	for (var r = 1; r < arguments.length; r++) {
		var t = null != arguments[r] ? arguments[r] : {};
		r % 2 ? ownKeys$2(Object(t), true).forEach(function(r) {
			_defineProperty$2(e, r, t[r]);
		}) : Object.getOwnPropertyDescriptors ? Object.defineProperties(e, Object.getOwnPropertyDescriptors(t)) : ownKeys$2(Object(t)).forEach(function(r) {
			Object.defineProperty(e, r, Object.getOwnPropertyDescriptor(t, r));
		});
	}
	return e;
}
function _defineProperty$2(e, r, t) {
	return (r = _toPropertyKey$2(r)) in e ? Object.defineProperty(e, r, {
		value: t,
		enumerable: true,
		configurable: true,
		writable: true
	}) : e[r] = t, e;
}
function _toPropertyKey$2(t) {
	var i = _toPrimitive$2(t, "string");
	return "symbol" == _typeof$2(i) ? i : i + "";
}
function _toPrimitive$2(t, r) {
	if ("object" != _typeof$2(t) || !t) return t;
	var e = t[Symbol.toPrimitive];
	if (void 0 !== e) {
		var i = e.call(t, r);
		if ("object" != _typeof$2(i)) return i;
		throw new TypeError("@@toPrimitive must return a primitive value.");
	}
	return ("string" === r ? String : Number)(t);
}
function tryOnMounted(fn) {
	var sync = arguments.length > 1 && arguments[1] !== void 0 ? arguments[1] : true;
	if (getCurrentInstance() && getCurrentInstance().components) onMounted(fn);
	else if (sync) fn();
	else nextTick(fn);
}
var _id = 0;
function useStyle(css) {
	var options = arguments.length > 1 && arguments[1] !== void 0 ? arguments[1] : {};
	var isLoaded = ref(false);
	var cssRef = ref(css);
	var styleRef = ref(null);
	var defaultDocument = tt() ? window.document : void 0;
	var _options$document = options.document, document = _options$document === void 0 ? defaultDocument : _options$document, _options$immediate = options.immediate, immediate = _options$immediate === void 0 ? true : _options$immediate, _options$manual = options.manual, manual = _options$manual === void 0 ? false : _options$manual, _options$name = options.name, name = _options$name === void 0 ? "style_".concat(++_id) : _options$name, _options$id = options.id, id = _options$id === void 0 ? void 0 : _options$id, _options$media = options.media, media = _options$media === void 0 ? void 0 : _options$media, _options$nonce = options.nonce, nonce = _options$nonce === void 0 ? void 0 : _options$nonce, _options$first = options.first, first = _options$first === void 0 ? false : _options$first, _options$onMounted = options.onMounted, onStyleMounted = _options$onMounted === void 0 ? void 0 : _options$onMounted, _options$onUpdated = options.onUpdated, onStyleUpdated = _options$onUpdated === void 0 ? void 0 : _options$onUpdated, _options$onLoad = options.onLoad, onStyleLoaded = _options$onLoad === void 0 ? void 0 : _options$onLoad, _options$props = options.props, props = _options$props === void 0 ? {} : _options$props;
	var stop = function stop() {};
	var load = function load(_css) {
		var _props = arguments.length > 1 && arguments[1] !== void 0 ? arguments[1] : {};
		if (!document) return;
		var _styleProps = _objectSpread$2(_objectSpread$2({}, props), _props);
		var _name = _styleProps.name || name, _id = _styleProps.id || id, _nonce = _styleProps.nonce || nonce;
		styleRef.value = document.querySelector("style[data-primevue-style-id=\"".concat(_name, "\"]")) || document.getElementById(_id) || document.createElement("style");
		if (!styleRef.value.isConnected) {
			cssRef.value = _css || css;
			A(styleRef.value, {
				type: "text/css",
				id: _id,
				media,
				nonce: _nonce
			});
			first ? document.head.prepend(styleRef.value) : document.head.appendChild(styleRef.value);
			_t(styleRef.value, "data-primevue-style-id", _name);
			A(styleRef.value, _styleProps);
			styleRef.value.onload = function(event) {
				return onStyleLoaded === null || onStyleLoaded === void 0 ? void 0 : onStyleLoaded(event, { name: _name });
			};
			onStyleMounted === null || onStyleMounted === void 0 || onStyleMounted(_name);
		}
		if (isLoaded.value) return;
		stop = watch(cssRef, function(value) {
			styleRef.value.textContent = value;
			onStyleUpdated === null || onStyleUpdated === void 0 || onStyleUpdated(_name);
		}, { immediate: true });
		isLoaded.value = true;
	};
	var unload = function unload() {
		if (!document || !isLoaded.value) return;
		stop();
		T(styleRef.value) && document.head.removeChild(styleRef.value);
		isLoaded.value = false;
		styleRef.value = null;
	};
	if (immediate && !manual) tryOnMounted(load);
	return {
		id,
		name,
		el: styleRef,
		css: cssRef,
		unload,
		load,
		isLoaded: readonly(isLoaded)
	};
}
//#endregion
//#region node_modules/@primevue/core/base/style/index.mjs
function _typeof$1(o) {
	"@babel/helpers - typeof";
	return _typeof$1 = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ? function(o) {
		return typeof o;
	} : function(o) {
		return o && "function" == typeof Symbol && o.constructor === Symbol && o !== Symbol.prototype ? "symbol" : typeof o;
	}, _typeof$1(o);
}
var _templateObject, _templateObject2, _templateObject3, _templateObject4;
function _slicedToArray(r, e) {
	return _arrayWithHoles(r) || _iterableToArrayLimit(r, e) || _unsupportedIterableToArray(r, e) || _nonIterableRest();
}
function _nonIterableRest() {
	throw new TypeError("Invalid attempt to destructure non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.");
}
function _unsupportedIterableToArray(r, a) {
	if (r) {
		if ("string" == typeof r) return _arrayLikeToArray(r, a);
		var t = {}.toString.call(r).slice(8, -1);
		return "Object" === t && r.constructor && (t = r.constructor.name), "Map" === t || "Set" === t ? Array.from(r) : "Arguments" === t || /^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(t) ? _arrayLikeToArray(r, a) : void 0;
	}
}
function _arrayLikeToArray(r, a) {
	(null == a || a > r.length) && (a = r.length);
	for (var e = 0, n = Array(a); e < a; e++) n[e] = r[e];
	return n;
}
function _iterableToArrayLimit(r, l) {
	var t = null == r ? null : "undefined" != typeof Symbol && r[Symbol.iterator] || r["@@iterator"];
	if (null != t) {
		var e, n, i, u, a = [], f = true, o = false;
		try {
			if (i = (t = t.call(r)).next, 0 === l);
			else for (; !(f = (e = i.call(t)).done) && (a.push(e.value), a.length !== l); f = !0);
		} catch (r) {
			o = true, n = r;
		} finally {
			try {
				if (!f && null != t["return"] && (u = t["return"](), Object(u) !== u)) return;
			} finally {
				if (o) throw n;
			}
		}
		return a;
	}
}
function _arrayWithHoles(r) {
	if (Array.isArray(r)) return r;
}
function ownKeys$1(e, r) {
	var t = Object.keys(e);
	if (Object.getOwnPropertySymbols) {
		var o = Object.getOwnPropertySymbols(e);
		r && (o = o.filter(function(r) {
			return Object.getOwnPropertyDescriptor(e, r).enumerable;
		})), t.push.apply(t, o);
	}
	return t;
}
function _objectSpread$1(e) {
	for (var r = 1; r < arguments.length; r++) {
		var t = null != arguments[r] ? arguments[r] : {};
		r % 2 ? ownKeys$1(Object(t), true).forEach(function(r) {
			_defineProperty$1(e, r, t[r]);
		}) : Object.getOwnPropertyDescriptors ? Object.defineProperties(e, Object.getOwnPropertyDescriptors(t)) : ownKeys$1(Object(t)).forEach(function(r) {
			Object.defineProperty(e, r, Object.getOwnPropertyDescriptor(t, r));
		});
	}
	return e;
}
function _defineProperty$1(e, r, t) {
	return (r = _toPropertyKey$1(r)) in e ? Object.defineProperty(e, r, {
		value: t,
		enumerable: true,
		configurable: true,
		writable: true
	}) : e[r] = t, e;
}
function _toPropertyKey$1(t) {
	var i = _toPrimitive$1(t, "string");
	return "symbol" == _typeof$1(i) ? i : i + "";
}
function _toPrimitive$1(t, r) {
	if ("object" != _typeof$1(t) || !t) return t;
	var e = t[Symbol.toPrimitive];
	if (void 0 !== e) {
		var i = e.call(t, r);
		if ("object" != _typeof$1(i)) return i;
		throw new TypeError("@@toPrimitive must return a primitive value.");
	}
	return ("string" === r ? String : Number)(t);
}
function _taggedTemplateLiteral(e, t) {
	return t || (t = e.slice(0)), Object.freeze(Object.defineProperties(e, { raw: { value: Object.freeze(t) } }));
}
var BaseStyle = {
	name: "base",
	css: function css(_ref) {
		var dt = _ref.dt;
		return "\n.p-hidden-accessible {\n    border: 0;\n    clip: rect(0 0 0 0);\n    height: 1px;\n    margin: -1px;\n    opacity: 0;\n    overflow: hidden;\n    padding: 0;\n    pointer-events: none;\n    position: absolute;\n    white-space: nowrap;\n    width: 1px;\n}\n\n.p-overflow-hidden {\n    overflow: hidden;\n    padding-right: ".concat(dt("scrollbar.width"), ";\n}\n");
	},
	style,
	classes: {},
	inlineStyles: {},
	load: function load(style) {
		var options = arguments.length > 1 && arguments[1] !== void 0 ? arguments[1] : {};
		var computedStyle = (arguments.length > 2 && arguments[2] !== void 0 ? arguments[2] : function(cs) {
			return cs;
		})(ar(_templateObject || (_templateObject = _taggedTemplateLiteral(["", ""])), style));
		return s$1(computedStyle) ? useStyle(Y$1(computedStyle), _objectSpread$1({ name: this.name }, options)) : {};
	},
	loadCSS: function loadCSS() {
		var options = arguments.length > 0 && arguments[0] !== void 0 ? arguments[0] : {};
		return this.load(this.css, options);
	},
	loadStyle: function loadStyle() {
		var _this = this;
		var options = arguments.length > 0 && arguments[0] !== void 0 ? arguments[0] : {};
		var style = arguments.length > 1 && arguments[1] !== void 0 ? arguments[1] : "";
		return this.load(this.style, options, function() {
			var computedStyle = arguments.length > 0 && arguments[0] !== void 0 ? arguments[0] : "";
			return S.transformCSS(options.name || _this.name, "".concat(computedStyle).concat(ar(_templateObject2 || (_templateObject2 = _taggedTemplateLiteral(["", ""])), style)));
		});
	},
	getCommonTheme: function getCommonTheme(params) {
		return S.getCommon(this.name, params);
	},
	getComponentTheme: function getComponentTheme(params) {
		return S.getComponent(this.name, params);
	},
	getDirectiveTheme: function getDirectiveTheme(params) {
		return S.getDirective(this.name, params);
	},
	getPresetTheme: function getPresetTheme(preset, selector, params) {
		return S.getCustomPreset(this.name, preset, selector, params);
	},
	getLayerOrderThemeCSS: function getLayerOrderThemeCSS() {
		return S.getLayerOrderCSS(this.name);
	},
	getStyleSheet: function getStyleSheet() {
		var extendedCSS = arguments.length > 0 && arguments[0] !== void 0 ? arguments[0] : "";
		var props = arguments.length > 1 && arguments[1] !== void 0 ? arguments[1] : {};
		if (this.css) {
			var _css = m(this.css, { dt: E }) || "";
			var _style = Y$1(ar(_templateObject3 || (_templateObject3 = _taggedTemplateLiteral([
				"",
				"",
				""
			])), _css, extendedCSS));
			var _props = Object.entries(props).reduce(function(acc, _ref2) {
				var _ref3 = _slicedToArray(_ref2, 2), k = _ref3[0], v = _ref3[1];
				return acc.push("".concat(k, "=\"").concat(v, "\"")) && acc;
			}, []).join(" ");
			return s$1(_style) ? "<style type=\"text/css\" data-primevue-style-id=\"".concat(this.name, "\" ").concat(_props, ">").concat(_style, "</style>") : "";
		}
		return "";
	},
	getCommonThemeStyleSheet: function getCommonThemeStyleSheet(params) {
		var props = arguments.length > 1 && arguments[1] !== void 0 ? arguments[1] : {};
		return S.getCommonStyleSheet(this.name, params, props);
	},
	getThemeStyleSheet: function getThemeStyleSheet(params) {
		var props = arguments.length > 1 && arguments[1] !== void 0 ? arguments[1] : {};
		var css = [S.getStyleSheet(this.name, params, props)];
		if (this.style) {
			var name = this.name === "base" ? "global-style" : "".concat(this.name, "-style");
			var _css = ar(_templateObject4 || (_templateObject4 = _taggedTemplateLiteral(["", ""])), m(this.style, { dt: E }));
			var _style = Y$1(S.transformCSS(name, _css));
			var _props = Object.entries(props).reduce(function(acc, _ref4) {
				var _ref5 = _slicedToArray(_ref4, 2), k = _ref5[0], v = _ref5[1];
				return acc.push("".concat(k, "=\"").concat(v, "\"")) && acc;
			}, []).join(" ");
			s$1(_style) && css.push("<style type=\"text/css\" data-primevue-style-id=\"".concat(name, "\" ").concat(_props, ">").concat(_style, "</style>"));
		}
		return css.join("");
	},
	extend: function extend(inStyle) {
		return _objectSpread$1(_objectSpread$1({}, this), {}, {
			css: void 0,
			style: void 0
		}, inStyle);
	}
};
//#endregion
//#region node_modules/@primevue/core/service/index.mjs
var PrimeVueService = s();
//#endregion
//#region node_modules/@primevue/core/config/index.mjs
function _typeof(o) {
	"@babel/helpers - typeof";
	return _typeof = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ? function(o) {
		return typeof o;
	} : function(o) {
		return o && "function" == typeof Symbol && o.constructor === Symbol && o !== Symbol.prototype ? "symbol" : typeof o;
	}, _typeof(o);
}
function ownKeys(e, r) {
	var t = Object.keys(e);
	if (Object.getOwnPropertySymbols) {
		var o = Object.getOwnPropertySymbols(e);
		r && (o = o.filter(function(r) {
			return Object.getOwnPropertyDescriptor(e, r).enumerable;
		})), t.push.apply(t, o);
	}
	return t;
}
function _objectSpread(e) {
	for (var r = 1; r < arguments.length; r++) {
		var t = null != arguments[r] ? arguments[r] : {};
		r % 2 ? ownKeys(Object(t), true).forEach(function(r) {
			_defineProperty(e, r, t[r]);
		}) : Object.getOwnPropertyDescriptors ? Object.defineProperties(e, Object.getOwnPropertyDescriptors(t)) : ownKeys(Object(t)).forEach(function(r) {
			Object.defineProperty(e, r, Object.getOwnPropertyDescriptor(t, r));
		});
	}
	return e;
}
function _defineProperty(e, r, t) {
	return (r = _toPropertyKey(r)) in e ? Object.defineProperty(e, r, {
		value: t,
		enumerable: true,
		configurable: true,
		writable: true
	}) : e[r] = t, e;
}
function _toPropertyKey(t) {
	var i = _toPrimitive(t, "string");
	return "symbol" == _typeof(i) ? i : i + "";
}
function _toPrimitive(t, r) {
	if ("object" != _typeof(t) || !t) return t;
	var e = t[Symbol.toPrimitive];
	if (void 0 !== e) {
		var i = e.call(t, r);
		if ("object" != _typeof(i)) return i;
		throw new TypeError("@@toPrimitive must return a primitive value.");
	}
	return ("string" === r ? String : Number)(t);
}
var defaultOptions = {
	ripple: false,
	inputStyle: null,
	inputVariant: null,
	locale: {
		startsWith: "Starts with",
		contains: "Contains",
		notContains: "Not contains",
		endsWith: "Ends with",
		equals: "Equals",
		notEquals: "Not equals",
		noFilter: "No Filter",
		lt: "Less than",
		lte: "Less than or equal to",
		gt: "Greater than",
		gte: "Greater than or equal to",
		dateIs: "Date is",
		dateIsNot: "Date is not",
		dateBefore: "Date is before",
		dateAfter: "Date is after",
		clear: "Clear",
		apply: "Apply",
		matchAll: "Match All",
		matchAny: "Match Any",
		addRule: "Add Rule",
		removeRule: "Remove Rule",
		accept: "Yes",
		reject: "No",
		choose: "Choose",
		upload: "Upload",
		cancel: "Cancel",
		completed: "Completed",
		pending: "Pending",
		fileSizeTypes: [
			"B",
			"KB",
			"MB",
			"GB",
			"TB",
			"PB",
			"EB",
			"ZB",
			"YB"
		],
		dayNames: [
			"Sunday",
			"Monday",
			"Tuesday",
			"Wednesday",
			"Thursday",
			"Friday",
			"Saturday"
		],
		dayNamesShort: [
			"Sun",
			"Mon",
			"Tue",
			"Wed",
			"Thu",
			"Fri",
			"Sat"
		],
		dayNamesMin: [
			"Su",
			"Mo",
			"Tu",
			"We",
			"Th",
			"Fr",
			"Sa"
		],
		monthNames: [
			"January",
			"February",
			"March",
			"April",
			"May",
			"June",
			"July",
			"August",
			"September",
			"October",
			"November",
			"December"
		],
		monthNamesShort: [
			"Jan",
			"Feb",
			"Mar",
			"Apr",
			"May",
			"Jun",
			"Jul",
			"Aug",
			"Sep",
			"Oct",
			"Nov",
			"Dec"
		],
		chooseYear: "Choose Year",
		chooseMonth: "Choose Month",
		chooseDate: "Choose Date",
		prevDecade: "Previous Decade",
		nextDecade: "Next Decade",
		prevYear: "Previous Year",
		nextYear: "Next Year",
		prevMonth: "Previous Month",
		nextMonth: "Next Month",
		prevHour: "Previous Hour",
		nextHour: "Next Hour",
		prevMinute: "Previous Minute",
		nextMinute: "Next Minute",
		prevSecond: "Previous Second",
		nextSecond: "Next Second",
		am: "am",
		pm: "pm",
		today: "Today",
		weekHeader: "Wk",
		firstDayOfWeek: 0,
		showMonthAfterYear: false,
		dateFormat: "mm/dd/yy",
		weak: "Weak",
		medium: "Medium",
		strong: "Strong",
		passwordPrompt: "Enter a password",
		emptyFilterMessage: "No results found",
		searchMessage: "{0} results are available",
		selectionMessage: "{0} items selected",
		emptySelectionMessage: "No selected item",
		emptySearchMessage: "No results found",
		fileChosenMessage: "{0} files",
		noFileChosenMessage: "No file chosen",
		emptyMessage: "No available options",
		aria: {
			trueLabel: "True",
			falseLabel: "False",
			nullLabel: "Not Selected",
			star: "1 star",
			stars: "{star} stars",
			selectAll: "All items selected",
			unselectAll: "All items unselected",
			close: "Close",
			previous: "Previous",
			next: "Next",
			navigation: "Navigation",
			scrollTop: "Scroll Top",
			moveTop: "Move Top",
			moveUp: "Move Up",
			moveDown: "Move Down",
			moveBottom: "Move Bottom",
			moveToTarget: "Move to Target",
			moveToSource: "Move to Source",
			moveAllToTarget: "Move All to Target",
			moveAllToSource: "Move All to Source",
			pageLabel: "Page {page}",
			firstPageLabel: "First Page",
			lastPageLabel: "Last Page",
			nextPageLabel: "Next Page",
			prevPageLabel: "Previous Page",
			rowsPerPageLabel: "Rows per page",
			jumpToPageDropdownLabel: "Jump to Page Dropdown",
			jumpToPageInputLabel: "Jump to Page Input",
			selectRow: "Row Selected",
			unselectRow: "Row Unselected",
			expandRow: "Row Expanded",
			collapseRow: "Row Collapsed",
			showFilterMenu: "Show Filter Menu",
			hideFilterMenu: "Hide Filter Menu",
			filterOperator: "Filter Operator",
			filterConstraint: "Filter Constraint",
			editRow: "Row Edit",
			saveEdit: "Save Edit",
			cancelEdit: "Cancel Edit",
			listView: "List View",
			gridView: "Grid View",
			slide: "Slide",
			slideNumber: "{slideNumber}",
			zoomImage: "Zoom Image",
			zoomIn: "Zoom In",
			zoomOut: "Zoom Out",
			rotateRight: "Rotate Right",
			rotateLeft: "Rotate Left",
			listLabel: "Option List"
		}
	},
	filterMatchModeOptions: {
		text: [
			FilterMatchMode.STARTS_WITH,
			FilterMatchMode.CONTAINS,
			FilterMatchMode.NOT_CONTAINS,
			FilterMatchMode.ENDS_WITH,
			FilterMatchMode.EQUALS,
			FilterMatchMode.NOT_EQUALS
		],
		numeric: [
			FilterMatchMode.EQUALS,
			FilterMatchMode.NOT_EQUALS,
			FilterMatchMode.LESS_THAN,
			FilterMatchMode.LESS_THAN_OR_EQUAL_TO,
			FilterMatchMode.GREATER_THAN,
			FilterMatchMode.GREATER_THAN_OR_EQUAL_TO
		],
		date: [
			FilterMatchMode.DATE_IS,
			FilterMatchMode.DATE_IS_NOT,
			FilterMatchMode.DATE_BEFORE,
			FilterMatchMode.DATE_AFTER
		]
	},
	zIndex: {
		modal: 1100,
		overlay: 1e3,
		menu: 1e3,
		tooltip: 1100
	},
	theme: void 0,
	unstyled: false,
	pt: void 0,
	ptOptions: {
		mergeSections: true,
		mergeProps: false
	},
	csp: { nonce: void 0 }
};
var PrimeVueSymbol = Symbol();
function usePrimeVue() {
	var PrimeVue = inject(PrimeVueSymbol);
	if (!PrimeVue) throw new Error("PrimeVue is not installed!");
	return PrimeVue;
}
function setup(app, options) {
	var PrimeVue = { config: reactive(options) };
	app.config.globalProperties.$primevue = PrimeVue;
	app.provide(PrimeVueSymbol, PrimeVue);
	clearConfig();
	setupConfig(app, PrimeVue);
	return PrimeVue;
}
var stopWatchers = [];
function clearConfig() {
	N.clear();
	stopWatchers.forEach(function(fn) {
		return fn === null || fn === void 0 ? void 0 : fn();
	});
	stopWatchers = [];
}
function setupConfig(app, PrimeVue) {
	var isThemeChanged = ref(false);
	/*** Methods and Services ***/
	var loadCommonTheme = function loadCommonTheme() {
		var _PrimeVue$config;
		if (((_PrimeVue$config = PrimeVue.config) === null || _PrimeVue$config === void 0 ? void 0 : _PrimeVue$config.theme) === "none") return;
		if (!S.isStyleNameLoaded("common")) {
			var _BaseStyle$getCommonT, _PrimeVue$config2;
			var _ref = ((_BaseStyle$getCommonT = BaseStyle.getCommonTheme) === null || _BaseStyle$getCommonT === void 0 ? void 0 : _BaseStyle$getCommonT.call(BaseStyle)) || {}, primitive = _ref.primitive, semantic = _ref.semantic, global = _ref.global, style = _ref.style;
			var styleOptions = { nonce: (_PrimeVue$config2 = PrimeVue.config) === null || _PrimeVue$config2 === void 0 || (_PrimeVue$config2 = _PrimeVue$config2.csp) === null || _PrimeVue$config2 === void 0 ? void 0 : _PrimeVue$config2.nonce };
			BaseStyle.load(primitive === null || primitive === void 0 ? void 0 : primitive.css, _objectSpread({ name: "primitive-variables" }, styleOptions));
			BaseStyle.load(semantic === null || semantic === void 0 ? void 0 : semantic.css, _objectSpread({ name: "semantic-variables" }, styleOptions));
			BaseStyle.load(global === null || global === void 0 ? void 0 : global.css, _objectSpread({ name: "global-variables" }, styleOptions));
			BaseStyle.loadStyle(_objectSpread({ name: "global-style" }, styleOptions), style);
			S.setLoadedStyleName("common");
		}
	};
	N.on("theme:change", function(newTheme) {
		if (!isThemeChanged.value) {
			app.config.globalProperties.$primevue.config.theme = newTheme;
			isThemeChanged.value = true;
		}
	});
	/*** Watchers ***/
	var stopConfigWatcher = watch(PrimeVue.config, function(newValue, oldValue) {
		PrimeVueService.emit("config:change", {
			newValue,
			oldValue
		});
	}, {
		immediate: true,
		deep: true
	});
	var stopRippleWatcher = watch(function() {
		return PrimeVue.config.ripple;
	}, function(newValue, oldValue) {
		PrimeVueService.emit("config:ripple:change", {
			newValue,
			oldValue
		});
	}, {
		immediate: true,
		deep: true
	});
	var stopThemeWatcher = watch(function() {
		return PrimeVue.config.theme;
	}, function(newValue, oldValue) {
		if (!isThemeChanged.value) S.setTheme(newValue);
		if (!PrimeVue.config.unstyled) loadCommonTheme();
		isThemeChanged.value = false;
		PrimeVueService.emit("config:theme:change", {
			newValue,
			oldValue
		});
	}, {
		immediate: true,
		deep: false
	});
	var stopUnstyledWatcher = watch(function() {
		return PrimeVue.config.unstyled;
	}, function(newValue, oldValue) {
		if (!newValue && PrimeVue.config.theme) loadCommonTheme();
		PrimeVueService.emit("config:unstyled:change", {
			newValue,
			oldValue
		});
	}, {
		immediate: true,
		deep: true
	});
	stopWatchers.push(stopConfigWatcher);
	stopWatchers.push(stopRippleWatcher);
	stopWatchers.push(stopThemeWatcher);
	stopWatchers.push(stopUnstyledWatcher);
}
var PrimeVue = { install: function install(app, options) {
	setup(app, H(defaultOptions, options));
} };
//#endregion
export { clearConfig, PrimeVue as default, defaultOptions, setup, setupConfig, usePrimeVue };

//# sourceMappingURL=primevue_config.js.map