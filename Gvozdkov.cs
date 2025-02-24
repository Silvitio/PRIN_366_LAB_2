using EA.Core.Interfaces;
using EA.Weapons;
using System.Collections.Generic;
using UnityEngine;
namespace EA.Core.Managers
{
    public interface ILevelManager
    {
        public bool IsToTitleScreen { get; set; }
        public bool IsMenuPaused { get; set; }
        public bool IsTutor { get; set; }

        public int WeaponIndex { get; }

        public void SetHealth(float health);

        public Rifle Rifle { get; set; }
        public Pistol Pistol { get; set; }
        public TwoPistols TwoPistols { get; set; }

        public void AddWeapon(int index);
        public void RemoveWeapon(int index);

        public bool IsPaused();
        public bool isTutored();

        public void SetLevelSettings(int killedBotsForWin, int coinRewardForWin, IMenu menuLogicManager, AudioClip winSound, AudioClip lossSound);

        public IWeapon GetWeapon();

        public void SetWeapon(int newIndex);

        public void InvokeBotKilled();

        public void TriggerLossScreen();

        public void ArrangeWeapons();

        public void Constructor(MusicManager musicManager);
        void SetAmmoData(int weaponCurrentAmmo, int weaponTotalAmmo, int weaponMagazineCapacity);
    }

    public class LevelManager : IService, ILevelManager
    {
        public bool IsToTitleScreen { get => _isToTitleScreen; set => _isToTitleScreen = value; }
        public bool IsMenuPaused { get; set; }

        private bool _isToTitleScreen = true;

        private ISoundFXManager _soundFXManager;
        private ISaveSystem _saveSystem;
        private ICurrencyManager _currencyManager;
        private IProgressManager _progressManager;

        private IMenu _menuLogicManager;

        private int _killedBotsForWin = 0;
        private int _killedBots = 0;
        private int _coinRewardForWin = 0;

        public void SetHealth(float health)
        {
            _menuLogicManager.SetHeath(health);
        }

        public Rifle Rifle { get; set; }
        public Pistol Pistol { get; set; }
        public TwoPistols TwoPistols { get; set; }

        private readonly List<IWeapon> _currentWeapons = new List<IWeapon>();

        public int WeaponIndex { get; private set; } = 0;

        private AudioClip _winSound;
        private AudioClip _lossSound;
        private SoundMovement _soundMovement;
        private MusicManager _musicManager;

        public void Constructor()
        {
            _soundFXManager = Game.Instance.GetSoundFXManager();
            _saveSystem = Game.Instance.GetSaveSystem();
            _currencyManager = Game.Instance.GetCurrencyManager();
            _progressManager = Game.Instance.GetProgressManager();
        }

        public void Constructor(MusicManager musicManager) { _musicManager = musicManager; }
        public void SetAmmoData(int weaponCurrentAmmo, int weaponTotalAmmo, int weaponMagazineCapacity)
        {
            _menuLogicManager.setAmmoData(weaponCurrentAmmo, weaponTotalAmmo, weaponMagazineCapacity);
        }

        public bool IsPaused()
        {
            return IsMenuPaused || !_menuLogicManager.IsGame;
            
        }

        public IWeapon GetWeapon()
        {
            if (WeaponIndex == 0) return Rifle;
            else if (WeaponIndex == 1) return Pistol;
            else if (WeaponIndex == 2) return TwoPistols;
            else return null;
        }

        public void SetLevelSettings(int killedBotsForWin, int coinRewardForWin, IMenu menuLogicManager, AudioClip winSound, AudioClip lossSound)
        {
            _currentWeapons.Clear();

            SetKilledBotsForWin(killedBotsForWin);
            _menuLogicManager = menuLogicManager;
            _coinRewardForWin = coinRewardForWin;
            _winSound = winSound;
            _lossSound = lossSound;
        }

        public void AddWeapon(int index)
        {
            if (index == 0) _currentWeapons.Add(Rifle);
            else if (index == 1) _currentWeapons.Add(Pistol);
            else if (index == 2) _currentWeapons.Add(TwoPistols);
        }

        public void RemoveWeapon(int index)
        {
            if (index == 0) _currentWeapons.Remove(Rifle);
            else if (index == 1) _currentWeapons.Remove(Pistol);
            else if (index == 2) _currentWeapons.Remove(TwoPistols);
        }

        public void SetWeapon(int newIndex)
        {
            if (_currentWeapons[newIndex] is Rifle) WeaponIndex = 0;
            else if (_currentWeapons[newIndex] is Pistol) WeaponIndex = 1;
            else if (_currentWeapons[newIndex] is TwoPistols) WeaponIndex = 2;

            _menuLogicManager.UpdateChosenWeapon();
        }

        public void InvokeBotKilled()
        {
            Debug.Log("Invoking Bot Killed");
            _killedBots++;
            CheckWin();
        }

        public void ArrangeWeapons()
        {
            if (_currentWeapons[1] is Rifle)
            {
                (_currentWeapons[1], _currentWeapons[0]) = (_currentWeapons[0], _currentWeapons[1]);
            }

            if (_currentWeapons[0] is TwoPistols)
            {
                (_currentWeapons[1], _currentWeapons[0]) = (_currentWeapons[0], _currentWeapons[1]);
            }
        }

        public void TriggerLossScreen()
        {
            PauseGameplay();
            
            _menuLogicManager.FinishLevel(false);

            PlaySound(_lossSound);

            StopMusic(); // Остановка музыки с затуханием в 1 секунду

            Reset();
        }


        private void TriggerWinScreen()
        {
            PauseGameplay();

            _menuLogicManager.FinishLevel(true);

            PlaySound(_winSound);

            StopMusic(); // Остановка музыки с затуханием в 1 секунду

            _currencyManager.AddCurrency(_coinRewardForWin);
            _progressManager.LevelPassed(SceneLoader.GetActiveSceneBuildIndex());
            _saveSystem.Save();

            _menuLogicManager.UpdateCoinValue();

            Reset();
        }


        private void PlaySound(AudioClip audioClip)
        {
            _soundFXManager.PlaySoundFXClip(audioClip, _menuLogicManager.GetTransform(), 1f);
        }

        private void StopMusic()
        {
            _musicManager?.StopMusic(); // Проверяем, есть ли MusicManager, и вызываем метод
        }

        private void PauseGameplay()
        {
            
            Time.timeScale = 0f;
            IsMenuPaused = true;
            _menuLogicManager.OnPauseGame.Invoke();
            _musicManager?.StopMusic();
        }

        private void CheckWin()
        {
            Debug.Log(_killedBots);
            Debug.Log(_killedBotsForWin);
            if (_killedBots == _killedBotsForWin)
            {
                TriggerWinScreen();
            }
        }

        private void SetKilledBotsForWin(int value)
        {
            _killedBotsForWin = value;
            _killedBots = 0;
        }

        private void Reset()
        {
            SetKilledBotsForWin(0);

            _coinRewardForWin = 0;

            _menuLogicManager = null;
            _winSound = null;
            _lossSound = null;
        }
    }
}
