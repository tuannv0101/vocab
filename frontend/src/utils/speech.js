// Web Speech API utility for English pronunciation

let preferredVoice = null;

function loadVoice() {
  if (typeof window === 'undefined' || !window.speechSynthesis) return;
  const voices = window.speechSynthesis.getVoices();
  preferredVoice = voices.find(v => v.lang.startsWith('en-US') && !v.name.includes('Bad')) ||
                   voices.find(v => v.lang.startsWith('en')) ||
                   null;
}

if (typeof window !== 'undefined' && window.speechSynthesis) {
  loadVoice();
  if (window.speechSynthesis.onvoiceschanged !== undefined) {
    window.speechSynthesis.onvoiceschanged = loadVoice;
  }
}

export function speakText(text, rate = 0.9) {
  if (typeof window === 'undefined' || !window.speechSynthesis || !text) return;

  let cleanText = text;
  if (cleanText.includes('(= sit)')) {
    cleanText = cleanText.replace('(= sit)', '');
  }

  window.speechSynthesis.cancel();

  const utterance = new SpeechSynthesisUtterance(cleanText);
  utterance.lang = 'en-US';
  utterance.rate = rate;
  if (preferredVoice) {
    utterance.voice = preferredVoice;
  }

  window.speechSynthesis.speak(utterance);
}
