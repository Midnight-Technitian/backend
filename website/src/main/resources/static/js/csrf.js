// CSRF helper for same-origin fetch requests with Spring Security's CookieCsrfTokenRepository
// - Reads XSRF-TOKEN cookie and attaches X-XSRF-TOKEN header on state-changing requests
// - Exposes window.csrfFetch and window.csrfJson convenience functions
// - Optional redirect to /auth/login when a 401 is received (set { redirectOn401: true })
(function() {
  function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
    return null;
  }

  function isSafeMethod(method) {
    const m = (method || 'GET').toUpperCase();
    return m === 'GET' || m === 'HEAD' || m === 'OPTIONS' || m === 'TRACE';
  }

  function isSameOrigin(url) {
    // Treat relative URLs as same-origin
    if (!/^https?:\/\//i.test(url)) return true;
    try {
      const loc = window.location;
      const u = new URL(url, loc.origin);
      return u.origin === loc.origin;
    } catch (_) {
      return false;
    }
  }

  async function csrfFetch(url, options = {}) {
    const opts = { ...options };
    const method = (opts.method || 'GET').toUpperCase();

    // Ensure cookies are sent for same-origin calls
    if (!opts.credentials && isSameOrigin(url)) {
      opts.credentials = 'same-origin';
    }

    if (!isSafeMethod(method) && isSameOrigin(url)) {
      const token = getCookie('XSRF-TOKEN');
      opts.headers = new Headers(opts.headers || {});
      if (token && !opts.headers.has('X-XSRF-TOKEN')) {
        opts.headers.set('X-XSRF-TOKEN', token);
      }
      // Default content-type for JSON if body is a plain object
      if (opts.body && typeof opts.body === 'object' && !(opts.body instanceof FormData)) {
        if (!opts.headers.has('Content-Type')) {
          opts.headers.set('Content-Type', 'application/json');
        }
      }
    }

    const resp = await fetch(url, opts);
    if (opts.redirectOn401 && resp.status === 401) {
      window.location.assign('/auth/login');
      return resp; // In case navigation is blocked
    }
    return resp;
  }

  async function csrfJson(url, { method = 'POST', body = {}, redirectOn401 = true } = {}) {
    return csrfFetch(url, {
      method,
      body: JSON.stringify(body),
      redirectOn401,
      headers: { 'Content-Type': 'application/json' }
    });
  }

  // Expose globally
  window.getCookie = getCookie;
  window.csrfFetch = csrfFetch;
  window.csrfJson = csrfJson;

  // Monkey-patch global fetch so existing code automatically carries CSRF for same-origin non-GET
  const originalFetch = window.fetch.bind(window);
  window.fetch = function(url, options) {
    return csrfFetch(url, options);
  };
})();
