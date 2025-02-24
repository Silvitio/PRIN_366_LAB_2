using UnityEngine;

public class BonusController : MonoBehaviour
{
    private Camera mainCamera;
    private Vector3 offset;
    private bool isSelected = false; // Флаг, обозначающий, выбран ли бонус
    private GameObject targetPipe;
    private bool isPositionLocked = true;

    private Vector3 initialPosition;

    public Sprite cleanPipeSprite;
    public Sprite cleanCornerPipeSprite;
    public Sprite cleanDiagonalLeftPipeSprite;
    public Sprite cleanDiagonalRightPipeSprite;
    public Sprite cleanCrossPipeDividedSprite;

    private void Start()
    {
        mainCamera = Camera.main;

        initialPosition = transform.position;
    }

    private void Update()
    {
        Vector2 mousePosition = mainCamera.ScreenToWorldPoint(Input.mousePosition);

        Collider2D hit = Physics2D.OverlapPoint(mousePosition);

        if (hit != null && hit.transform == transform)
        {
            isSelected = false;

            if (Input.GetKeyDown(KeyCode.Backspace) && !isPositionLocked)
            {
                ReturnToInitialPosition();
            }
        }
        else if (!Input.GetMouseButton(0))
        {
            isSelected = false;
        }

        if (isSelected && Input.GetMouseButtonDown(0))
        {
            offset = transform.position - new Vector3(mousePosition.x, mousePosition.y, transform.position.z);
        }

        if (isSelected && Input.GetMouseButton(0) && !isPositionLocked)
        {
            transform.position = new Vector3(mousePosition.x, mousePosition.y, transform.position.z) + offset;
        }

        if (Input.GetMouseButtonUp(0))
        {
            isSelected = false;

            HandlePipeAction();
        }
    }

    private void HandlePipeAction()
    {
        if (targetPipe != null)
        {
            if (targetPipe.CompareTag("DirtyPipe"))
            {
                CleanPipe("Pipe", targetPipe.tag);
            }
            else if (targetPipe.CompareTag("LeakPipe"))
            {
                CleanPipe("Pipe", targetPipe.tag);
            }
        }
    }
    private void CleanPipe(string newTag, string currentTag)
    {
        if (currentTag == "DirtyPipe")
        {
            Destroy(targetPipe.GetComponent<DirtyPipeController>());
        }
        else
        {
            Destroy(targetPipe.GetComponent<LeakPipeController>());
        }

        targetPipe.GetComponent<SpriteRenderer>().sprite = GetCleanSprite(targetPipe.name);     

        targetPipe.tag = newTag;

        Destroy(gameObject);
    }
    public void ReturnToInitialPosition()
    {
        transform.position = initialPosition;
    }

    private Sprite GetCleanSprite(string pipeName)
    {
        if (pipeName.Contains("DirtyPipe") || pipeName.Contains("LeakStraight")) return cleanPipeSprite;
        if (pipeName.Contains("DirtyCornerPipe") || pipeName.Contains("LeakCornerPipe")) return cleanCornerPipeSprite;
        if (pipeName.Contains("DirtyDiagonalPipeLeft") || pipeName.Contains("LeakDiagonalPipeLeft")) return cleanDiagonalLeftPipeSprite;
        if (pipeName.Contains("DirtyDiagonalPipeRight") || pipeName.Contains("LeakDiagonalPipeRight")) return cleanDiagonalRightPipeSprite;
        if (pipeName.Contains("DirtyCrossPipeDivided") || pipeName.Contains("LeakCrossPipeDivided")) return cleanCrossPipeDividedSprite;
        return null;
    }

    private void OnTriggerEnter2D(Collider2D other)
    {
        if (other.CompareTag("DirtyPipes") || other.CompareTag("LeakPipe"))
        {
            targetPipe = other.gameObject;
        }
    }

    private void OnTriggerExit2D(Collider2D other)
    {
        if (targetPipe == other.gameObject)
        {
            targetPipe = null;
        }
    }

    public void SetPositionLocked(bool locked)
    {
        isPositionLocked = locked;
    }

    public bool IsPositionLocked()
    {
        return isPositionLocked;
    }
}
