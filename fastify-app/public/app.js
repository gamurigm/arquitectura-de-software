const form = document.querySelector('#spotify-form');
const urlInput = document.querySelector('#spotify-url');
const statusMessage = document.querySelector('#form-status');
const playerContainer = document.querySelector('#player-container');
const emptyState = document.querySelector('#empty-state');

function buildSpotifyEmbedUrl(input) {
  const value = input.trim();
  const match = value.match(
    /^https:\/\/open\.spotify\.com\/(playlist|album)\/([A-Za-z0-9]+)(?:\?[^\s#]*)?(?:#[^\s]*)?$/i,
  );
  if (!match) {
    return null;
  }

  try {
    const url = new URL(value);
    const [, type, id] = match;
    if (
      url.protocol !== 'https:' ||
      url.hostname !== 'open.spotify.com' ||
      url.pathname !== `/${type}/${id}`
    ) {
      return null;
    }

    return `https://open.spotify.com/embed/${type.toLowerCase()}/${id}`;
  } catch {
    return null;
  }
}

form.addEventListener('submit', (event) => {
  event.preventDefault();

  const embedUrl = buildSpotifyEmbedUrl(urlInput.value);
  if (!embedUrl) {
    statusMessage.textContent = 'Pega un enlace válido de una playlist o un álbum de Spotify.';
    return;
  }

  statusMessage.textContent = '';

  const iframe = document.createElement('iframe');
  iframe.src = embedUrl;
  iframe.title = 'Reproductor de Spotify';
  iframe.width = '100%';
  iframe.height = '352';
  iframe.loading = 'lazy';
  iframe.setAttribute('allow', 'autoplay; clipboard-write; encrypted-media; fullscreen; picture-in-picture');
  iframe.setAttribute('allowfullscreen', '');

  playerContainer.replaceChildren(iframe);
  emptyState.hidden = true;
});
