
export function useScroll(scrollElement) {
  const scrollToBottom = async () => {
    if (scrollElement) {
      const scrollHeight = scrollElement.scrollHeight;
      const scrollTop = scrollElement.scrollTop;
      const clientHeight = scrollElement.clientHeight;

      if (scrollTop < scrollHeight - clientHeight) {
        // 当前位置不在底部
        scrollElement.scrollTo({
          top: scrollHeight,
          behavior: 'smooth' // 平滑滚动
        });
      }
    }
  };

  const scrollToTop = async () => {
    if (scrollElement) {
      scrollElement.scrollTop = 0;
    }
  };

  const scrollToBottomIfAtBottom = async () => {
    if (scrollElement) {
      const scrollHeight = scrollElement.scrollHeight;
      const scrollTop = scrollElement.scrollTop;
      const clientHeight = scrollElement.clientHeight;

      if (scrollTop < scrollHeight - clientHeight) {
        // 当前位置不在底部
        scrollElement.scrollTo({
          top: scrollHeight,
          behavior: 'smooth' // 平滑滚动
        });
      }
    }
  };

  return {
    scrollElement,
    scrollToBottom,
    scrollToTop,
    scrollToBottomIfAtBottom
  };
}
