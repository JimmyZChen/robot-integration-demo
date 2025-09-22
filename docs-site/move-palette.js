/* move-palette.js
 * 把主题切换（月亮）克隆到 Gitee 左边；兼容 navigation.instant
 */
(function () {
  function movePalette() {
    const header = document.querySelector('header.md-header');
    if (!header) return;

    // 找主题按钮（不同版本可能是 for="__palette" 或 data-md-toggle="palette"）
    const paletteBtn =
      header.querySelector('.md-header__button[for^="__palette"]') ||
      header.querySelector('.md-header__button[data-md-toggle="palette"]');

    // 右上角源码（Gitee）按钮的容器
    const source = header.querySelector('.md-header__source');

    if (!paletteBtn || !source) return;

    // 已经移动过就不重复
    if (header.querySelector('.js-moved-palette')) return;

    // 克隆一个放到 Gitee 左边，原位隐藏
    const clone = paletteBtn.cloneNode(true);
    clone.classList.add('js-moved-palette');
    source.parentNode.insertBefore(clone, source);
    paletteBtn.style.display = 'none';
  }

  // 支持 navigation.instant（Material 提供的事件流）
  if (window.document$ && typeof window.document$.subscribe === 'function') {
    window.document$.subscribe(movePalette);
  } else {
    document.addEventListener('DOMContentLoaded', movePalette);
  }
})();
